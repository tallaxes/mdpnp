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

import org.apache.camel.Exchange;

import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.TypeSupportImpl;

/**
 * Event driven (asynchronous) consumer for RTI Connext.
 */
public class RtiConsumerListener extends DataReaderAdapter {
    // private static final transient Logger LOG = LoggerFactory.getLogger(RtiConsumerListener.class);
    private RtiConsumer consumer;
    private TypeSupportImpl typeSupport;

    public RtiConsumerListener(RtiConsumer consumer, TypeSupportImpl typeSupport) {
        this.consumer = consumer;
        this.typeSupport = typeSupport;
    }

    public void on_data_available(DataReader reader) {
    	long now = System.nanoTime() / 1000;
    	while(true){
	        // create an object of the expected data type
	        Object data = typeSupport.create_data();
	        //System.out.println("data = " + data.getClass());
	        SampleInfo sampleInfo = new SampleInfo();
	
	        try {
	            reader.take_next_sample_untyped(data, sampleInfo);
	        } catch (RETCODE_NO_DATA r) {
	        	// no more data -- return
	        	return;
	        } catch (RETCODE_ERROR r) {
	            // TODO: handle
	        	return;
	        }
	
	        try {
	            Exchange exchange = consumer.getEndpoint().createExchange();
	            // add timestamp
	            exchange.getIn().setHeader("receiveTimestampUS", now);
	            
	            // TODO: set appropriate headers/properties
	            if (sampleInfo.valid_data) {
	                exchange.getIn().setBody(data);
	            }
	
	            // TODO: use the asynchronous routing engine?
	            consumer.getProcessor().process(exchange);
	        } catch (Exception e) {
	            // TODO handle
	        }
    	}
    }

    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
    }

    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
    }

    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
    }

    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
    }

    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
    }

    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
    }
}
