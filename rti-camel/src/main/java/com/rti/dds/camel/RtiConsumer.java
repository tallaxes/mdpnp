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

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.publication.DataWriterProtocolStatus;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.DataReaderProtocolStatus;
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
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Event driven (asynchronous) consumer for RTI Connext.
 */
public class RtiConsumer extends DefaultConsumer implements Runnable {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiConsumer.class);
	
    private RtiEndpointContext ddsContext;
    private Subscriber subscriber;
    private DataReader reader;
    private String topicName;
    private TypeSupportImpl _typeSupport;
    
    private WaitSet waitSet;
    private GuardCondition stopCondition;
    private boolean waitSetThreadFlag = false;
    private Thread waitSetThread = null;
    
    public RtiConsumer(RtiEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }


    @Override
    protected void doStart() throws Exception {
        ddsContext = ((RtiEndpoint)getEndpoint()).createDdsContext();
        if (ddsContext == null) {
            throw new CamelException("Cannot start RtiConsumer: invalid setup.");
        }

        // Create the data writer using the default publisher
        DomainParticipant p = ddsContext.getParticipant().getValue();
        _typeSupport = ddsContext.getParticipant().getRegisteredTypeSupport(ddsContext.getEndpoint().getRegisteredType());
        
        subscriber = p.create_subscriber(
        	getSubscriberQos(p, ((DdsInstanceData)getEndpoint()).getSubscriberQos()), 
            null, 
            StatusKind.STATUS_MASK_NONE);
        if (subscriber == null) {
            throw new CamelException("Cannot start RtiConsumer: invalid setup.");
        }
        
        
        // create waitset thread (if used)
        if( ((RtiEndpoint)getEndpoint()).isWaitSet() ){
        	waitSetThreadFlag = true;
        	reader = subscriber.create_datareader(
    	            ddsContext.getTopic().getValue(), 
    	            getDataReaderQos(p, ((DdsInstanceData)getEndpoint()).getDataReaderQos()),
    	            null, 
    	            StatusKind.STATUS_MASK_NONE);    
        	waitSetThread = new Thread(this);
        	waitSetThread.start();
        }else{
        	// use listener
            DataReaderListener listener = new RtiConsumerListener(this, _typeSupport); 
        	reader = subscriber.create_datareader(
	            ddsContext.getTopic().getValue(), 
	            getDataReaderQos(p, ((DdsInstanceData)getEndpoint()).getDataReaderQos()),
	            listener, 
	            StatusKind.DATA_AVAILABLE_STATUS);
        }
        if (reader == null) {
            throw new CamelException("Cannot start RtiConsumer: invalid setup.");
        }
        
        topicName = reader.get_topicdescription().get_name();
		LOG.info("RtiConsumer[" + topicName + "] started...");

        super.doStart();
    }

    protected void doStop() throws Exception {
        super.doStop();    

        if (ddsContext == null) {
        	return;
        }
        
        LOG.info("RtiConsumer[" + topicName + "] stopping...");
        DomainParticipant p = ddsContext.getParticipant().getValue();
        
        // stop wait set thread
        if(waitSetThreadFlag){
        	waitSetThreadFlag=false;
        	stopCondition.set_trigger_value(true);
        }
        
        reader.set_listener(null,StatusKind.STATUS_MASK_ALL);
        subscriber.delete_contained_entities();
        reader = null;
        if( p!=null){	// TODO: figure out why participant is null
        	p.delete_subscriber(subscriber);
        }	
        subscriber = null;
     
        ((RtiEndpoint)getEndpoint()).releaseDdsContext();
        ddsContext = null;               
    }

    @Override
    public Processor getProcessor() {
        return super.getProcessor();
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
    
    private void runWaitSetThread(){
    	LoanableSequence dataSeq = new LoanableSequence(_typeSupport.create_data().getClass());
    	SampleInfoSeq infoSeq = new SampleInfoSeq();

    	// create the WaitSet infrastructure
		waitSet = new WaitSet();
		StatusCondition statusCond = reader.get_statuscondition();
		statusCond.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		waitSet.attach_condition(statusCond);	
		
		// attach external event condition
		stopCondition = new GuardCondition();
		stopCondition.set_trigger_value(false);
		waitSet.attach_condition(stopCondition);
		
		Duration_t infiniteWait = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITY_NSEC);
    	
		LOG.info("RtiConsumer [" + topicName + "], WaitSet thread started...");	
		
    	while(waitSetThreadFlag){
			try{
		    	long now = System.nanoTime() / 1000;

		    	/*
		    	// check if samples are available
		    	reader.take_untyped(dataSeq, infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, 
						SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
						InstanceStateKind.ANY_INSTANCE_STATE);
				
		    	for(int i=0; i<infoSeq.size(); i++){
		    		SampleInfo info = (SampleInfo)infoSeq.get(i);
		    		if( info.valid_data ){
		    			// create exchange
			            Exchange exchange = this.getEndpoint().createExchange();
			            // TODO: set appropriate headers/properties
			            // add timestamp
			            exchange.getIn().setHeader("receiveTimestampUS", now);
			            
			            // copy data
			            Object data = _typeSupport.create_data(dataSeq.get(i));
			            // copy sample info
			            SampleInfo sampleInfo = new SampleInfo();
			            sampleInfo.copy_from(info);
			            
			            exchange.getIn().setBody(data);
				        // TODO: use the asynchronous routing engine?
				        this.getProcessor().process(exchange);
		    		}
				}
				*/
		        // create an object of the expected data type
		        Object data = _typeSupport.create_data();
		        //System.out.println("data = " + data.getClass());
		        SampleInfo sampleInfo = new SampleInfo();
		
		        reader.take_next_sample_untyped(data, sampleInfo);
		
		        try {
		            Exchange exchange = this.getEndpoint().createExchange();
		            // add timestamp
		            exchange.getIn().setHeader("receiveTimestampUS", now);
		            
		            // TODO: set appropriate headers/properties
		            if (sampleInfo.valid_data) {
		                exchange.getIn().setBody(data);
		            }
		
		            // TODO: use the asynchronous routing engine?
		            this.getProcessor().process(exchange);
		        } catch (Exception e) {
		            // TODO handle
		        }

			}catch (RETCODE_NO_DATA noData) {
				// wait until new data arrives
				try{
			    	ConditionSeq activeConditions = new ConditionSeq();
					waitSet.wait(activeConditions, infiniteWait);
				}catch(RETCODE_TIMEOUT r) {
					// don't care... shouldn't happen
				}catch(RETCODE_ERROR r) {
					// TODO: handle
				}
	        } catch (RETCODE_ERROR r) {
			} finally {
	        //	reader.return_loan_untyped(dataSeq, infoSeq);
	        }
    	}			
    	
		LOG.info("RtiConsumer [" + topicName + "], WaitSet thread stopped.");	
    }


	@Override
	public void run() {
		// TODO Auto-generated method stub
		runWaitSetThread();
	}
	
    public DataReaderProtocolStatus getProtocolStatus() {
    	DataReaderProtocolStatus status = null;
    	
    	if( reader != null ){
    		status = new DataReaderProtocolStatus();
    		reader.get_datareader_protocol_status(status);
    	}
    	
    	return status;
    }

}
