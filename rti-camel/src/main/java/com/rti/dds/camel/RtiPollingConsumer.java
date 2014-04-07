/**
 * Copyright 2013, Real-Time Innovations (RTI)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.rti.dds.camel;

import java.lang.reflect.Field;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_BAD_PARAMETER;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TypeSupportImpl;
import com.rti.dds.util.LoanableSequence;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.PollingConsumerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Event driven (asynchronous) consumer for RTI Connext.
 */
public class RtiPollingConsumer extends PollingConsumerSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiPollingConsumer.class);
    private static final long INFINITE_WAIT = Long.MAX_VALUE;
	
    private RtiEndpointContext ddsContext;
    private Subscriber subscriber;
    private DataReader reader;
    private TypeSupportImpl typeSupport;
    
    private WaitSet waitSet;
    private StatusCondition statusCond;
    private Object keyHolder;    
    private	LoanableSequence dataSeq;
    private SampleInfoSeq infoSeq;

    public RtiPollingConsumer(RtiEndpoint endpoint) {
    	super(endpoint);
    }

    @Override
    protected void doStart() throws Exception {
        ddsContext = ((RtiEndpoint)getEndpoint()).createDdsContext();
        if (ddsContext == null) {
            throw new CamelException("Cannot start RtiPollingConsumer: invalid setup.");
        }

        // Create the data reader using the default subscriber
        DomainParticipant p = ddsContext.getParticipant().getValue();
        typeSupport = ddsContext.getParticipant().getRegisteredTypeSupport(ddsContext.getEndpoint().getRegisteredType());
        
        subscriber = p.create_subscriber(
        	getSubscriberQos(p, ((DdsInstanceData)getEndpoint()).getSubscriberQos()), 
            null, 
            StatusKind.STATUS_MASK_NONE);
        if (subscriber == null) {
            throw new CamelException("Cannot start RtiPollingConsumer: invalid setup.");
        }
        
        // create the data reader -- no listener
        reader = subscriber.create_datareader(
            ddsContext.getTopic().getValue(), 
            getDataReaderQos(p, ((DdsInstanceData)getEndpoint()).getDataReaderQos()),
            null, 
            StatusKind.STATUS_MASK_NONE);
        if (reader == null) {
            throw new CamelException("Cannot start RtiPollingConsumer: invalid setup.");
        }
        
        // create the WaitSet infrastructure
		waitSet = new WaitSet();
		statusCond = reader.get_statuscondition();
		statusCond.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		waitSet.attach_condition(statusCond);

		// create object that can be reused when retrieving an instance handle
		keyHolder = typeSupport.create_data();
		dataSeq = new LoanableSequence(keyHolder.getClass());
		infoSeq = new SampleInfoSeq();
		
		LOG.info("RtiPollingConsumer(" + reader.get_topicdescription().get_name() + ") started...");
		
    }

    protected void doStop() throws Exception {
        
        if (ddsContext == null) {
        	return;
        }
        
        LOG.info("RtiPollingConsumer stopping...");
        DomainParticipant p = ddsContext.getParticipant().getValue();
        
        subscriber.delete_contained_entities();
        reader = null;
        if( p != null ){
        	p.delete_subscriber(subscriber);
        }
        subscriber = null;
        
        ((RtiEndpoint)getEndpoint()).releaseDdsContext();
        ddsContext = null;          
    }

    private SubscriberQos getSubscriberQos(DomainParticipant participant, DdsQosConfiguration config) {
    	SubscriberQos qos = new SubscriberQos();
    	participant.get_default_subscriber_qos(qos);
    	if (config != null && config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			DomainParticipantFactory.get_instance().get_subscriber_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, getEndpoint().getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	return qos;
    }


    private DataReaderQos getDataReaderQos(DomainParticipant participant, DdsQosConfiguration config) {
    	DataReaderQos qos = new DataReaderQos();
    	participant.get_default_datareader_qos(qos);
    	if (config != null && config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			DomainParticipantFactory.get_instance().get_datareader_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, getEndpoint().getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	return qos;
    }
    
    public String getKeyFromThreadLocalStorage() {
    	String tls = (String)RtiThreadLocalStorage.getThreadLocal().get();    	
    	if( tls == null || tls.isEmpty() ){
    		LOG.info("Thread local storage not set -- no key present");
    		return null;
    	}
    	
    	LOG.info("Thread local storage value: " + tls);
    	return tls;
    }
        
    public InstanceHandle_t getKeyHandle(String tls) {
    	if( tls == null ){
    		return InstanceHandle_t.HANDLE_NIL;
    	}
    	
    	String keyName = tls.substring(0, tls.indexOf("="));
    	String keyValue = tls.substring(tls.indexOf("=")+1);
    	
    	Class<?> clazz = keyHolder.getClass();
    	try {
        	// set key field
			Field keyField = clazz.getField(keyName);
			keyField.set(keyHolder, keyValue);
		} catch (Exception e) {
			// TODO -- Log event
			LOG.warn("Could not set key field " + keyName + " for type " + clazz);
			return InstanceHandle_t.HANDLE_NIL;
		} 
    	
    	// look for handle
    	return reader.lookup_instance_untyped(keyHolder);
    }
    
    public static void MillisecondsToDuration(long ms, Duration_t duration){
    	if (ms == INFINITE_WAIT){
    		duration.sec = Duration_t.DURATION_INFINITE_SEC;
    		duration.nanosec = Duration_t.DURATION_INFINITE_NSEC;
    	}else{    	
    		duration.sec = (int)ms/1000;
    		duration.nanosec = (int)(ms%1000)*1000000;    	
    	}
    }
        
    public Exchange waitForKeyedData(String key, long remainingMS){
    	Object data = null;
    	SampleInfo infoCopy = new SampleInfo();
    	boolean keepWaiting = true;
    	Duration_t waitDur = new Duration_t();    	
    	long start, end;
    	
    	InstanceHandle_t handle = InstanceHandle_t.HANDLE_NIL;
    	
    	while(keepWaiting){
			start = System.currentTimeMillis();
			try{
				if( handle.is_nil() ){
					handle = getKeyHandle(key);
					if( handle.is_nil() ){
						// handle still not known... skip the attempt to read
						throw new RETCODE_NO_DATA("nil handle");
					}
				}
				// first check if a sample is available
				reader.take_instance_untyped(dataSeq, infoSeq, 1, handle,
							SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
							InstanceStateKind.ANY_INSTANCE_STATE);
				
				// iterate over the sequence
				for(int i=0; i<dataSeq.size(); i++){
					SampleInfo info = (SampleInfo)infoSeq.get(i);
					if( info.valid_data ){
						// valid sample found
						data = typeSupport.create_data();
						data = typeSupport.copy_data(data, dataSeq.get(i));
						infoCopy.copy_from(info);

						Exchange exchange = super.getEndpoint().createExchange();
			    		exchange.getIn().setBody(data);
			    		// TODO: set appropriate headers/properties
			    		return exchange;
					}
				}
			}catch (RETCODE_NO_DATA noData) {
				if( remainingMS != INFINITE_WAIT && remainingMS <= 0 ){
					// don't wait for new data; give up now
					return null;
				}
				
				// wait for the given amount of time until data arrives
				try{
					ConditionSeq activeConditions = new ConditionSeq();
					MillisecondsToDuration(remainingMS, waitDur);
					waitSet.wait(activeConditions, waitDur);
					end = System.currentTimeMillis();

					// update remaining time because waitSet will be woken 
					//	when new data arrives for ANY key, but only update if not indefinite wait
					if( remainingMS != INFINITE_WAIT ){
						remainingMS -= (end - start);
					}					
				}catch(RETCODE_TIMEOUT r) {
					// waitSet timeout -- return null
					return null;
				}catch(RETCODE_ERROR r) {
					// TODO: handle
					keepWaiting = false;
				}
	        } catch(RETCODE_BAD_PARAMETER badParam){
	        	// thrown if the handle is NIL or unknown
	        	// ...just wait again
				if( remainingMS != INFINITE_WAIT && remainingMS <= 0 ){
					// don't wait for new data; give up now
					return null;
				}
				
				// wait for the given amount of time until data arrives
				try{
					ConditionSeq activeConditions = new ConditionSeq();
					MillisecondsToDuration(remainingMS, waitDur);
					waitSet.wait(activeConditions, waitDur);
					end = System.currentTimeMillis();

					// update remaining time because waitSet will be woken 
					//	when new data arrives for ANY key, but only update if not indefinite wait
					if( remainingMS != INFINITE_WAIT ){
						remainingMS -= (end - start);
					}					
				}catch(RETCODE_TIMEOUT r) {
					// waitSet timeout -- return null
					return null;
				}catch(RETCODE_ERROR r) {
					// TODO: handle
					keepWaiting = false;
				}	        	
	        } catch (RETCODE_ERROR r) {
	            // TODO: handle
	        	keepWaiting = false;
	        } finally {
	        	reader.return_loan_untyped(dataSeq, infoSeq);
	        }
    	}
	
    	return null;
    }

    public Exchange waitForData(long remainingMS){
    	Object data = typeSupport.create_data();
    	SampleInfo info = new SampleInfo();
    	Duration_t waitDur = new Duration_t();  
    	long start, end;
    	boolean keepWaiting = true;
    	long now = System.nanoTime() / 1000;
    	
    	while(keepWaiting){
    		start = System.currentTimeMillis();
			try{
				// check if a sample is available
				reader.take_next_sample_untyped(data, info);
				if( info.valid_data ){
					Exchange exchange = super.getEndpoint().createExchange();
					exchange.getIn().setBody(data);
					// TODO: set appropriate headers/properties
		            // add timestamp
		            exchange.getIn().setHeader("receiveTimestampUS", now);
					return exchange;
				}
			}catch (RETCODE_NO_DATA noData) {
				if( remainingMS != INFINITE_WAIT && remainingMS <= 0){
					// don't wait for new data; give up now
					return null;
				}
				
				// wait for the remaining amount of time until data arrives
				try{
					ConditionSeq activeConditions = new ConditionSeq();
					MillisecondsToDuration(remainingMS, waitDur);
					waitSet.wait(activeConditions, waitDur);
					end = System.currentTimeMillis();

					if( remainingMS != INFINITE_WAIT ){
						remainingMS -= (end - start);
					}
				}catch(RETCODE_TIMEOUT r) {
					// waitSet timeout -- return null
					return null;
				}catch(RETCODE_ERROR r) {
					// TODO: handle
					keepWaiting = false;
				}
	        } catch (RETCODE_ERROR r) {
	            // TODO: handle
	        	keepWaiting = false;
	        }
    	}
			
		return null;
    }    
    
	@Override
	public Exchange receive() {
		String key = getKeyFromThreadLocalStorage();
		LOG.info("receive(INF),key: "+key);
		if( key == null ){
			return waitForData(INFINITE_WAIT);
		}else{
			Exchange e = waitForKeyedData(key, INFINITE_WAIT);
			if( e != null ){
				LOG.info("receive(INF) returned " + e.getIn().getBody());
			}
			return e;
		}
	}


	@Override
	// waits until the given timeout for a message
	public Exchange receive(long timeout) {
		String key = getKeyFromThreadLocalStorage();
		LOG.info("receive("+timeout+"),key: "+key);
		if( key == null ){
			return waitForData(timeout);
		}else{
			Exchange e = waitForKeyedData(key,timeout);
			if( e != null ){
				LOG.info("receive("+timeout+") returned " + e.getIn().getBody());
			}			
			return e;
		}
	}


	@Override
	// returns immediately if a message is not available
	public Exchange receiveNoWait() { 
		String key = getKeyFromThreadLocalStorage();
		LOG.info("receiveNoWait,key: "+key);
		if( key == null ){
			return waitForData(0);
		}else{
			return waitForKeyedData(key, 0);
		}
	}
}
