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
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterProtocolStatus;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The HelloWorld producer.
 */
public class RtiProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiProducer.class);
    private RtiEndpointContext ddsContext;
    private Publisher publisher;
    private DataWriter writer;
    private String topicName;

    public RtiProducer(RtiEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doStart() throws Exception {
        ddsContext = ((RtiEndpoint)getEndpoint()).createDdsContext();
        if (ddsContext == null) {
            throw new CamelException("Cannot start RtiProducer: invalid setup.");
        }

        // Create the data writer using the default publisher
        DomainParticipant p = ddsContext.getParticipant().getValue();
        // TypeSupportImpl typeSupport = ddsContext.getParticipant().getRegisteredTypeSupport(ddsContext.getEndpoint().getRegisteredType());

        publisher = p.create_publisher(
        	getPublisherQos(p, ((DdsInstanceData)getEndpoint()).getPublisherQos()), 
            null, 
            StatusKind.STATUS_MASK_NONE);
        if (publisher == null) {
            throw new CamelException("Cannot start RtiProducer: invalid setup.");
        }

        writer = publisher.create_datawriter(
            ddsContext.getTopic().getValue(),
            getDataWriterQos(p, ((DdsInstanceData)getEndpoint()).getDataWriterQos()),
            null, // listener
            StatusKind.STATUS_MASK_NONE);
        if (writer == null) {
            LOG.info("Unable to create data writer\n");
            return;
        }
        
        topicName = writer.get_topic().get_name();

        LOG.info("RtiProducer[" + topicName + "] starting...");

        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        
        if (ddsContext == null) {
        	return;
        }        
        
        LOG.info("RtiProducer[" + topicName + "] stopping...");
        // delete the writer/publisher
        DomainParticipant p = ddsContext.getParticipant().getValue();
        publisher.delete_contained_entities();
        writer = null;
        if( p!= null){
        	p.delete_publisher(publisher);
        }
        publisher = null;
        
        LOG.info("Producer releasing context");
        ((RtiEndpoint)getEndpoint()).releaseDdsContext();
        ddsContext = null;       
     }

    @Override
    public void process(Exchange exchange) throws Exception {
    	Object body = exchange.getIn().getBody();
    	if(LOG.isTraceEnabled()) {
	        LOG.trace("RtiProducer[" + topicName + "]: sync send request");
	        LOG.trace("RtiProducer[" + topicName + "]: body type: " + body.getClass().getName());
	        LOG.trace("RtiProducer[" + topicName + "]: body: " + body.toString());
    	}
        writer.write_untyped(body, InstanceHandle_t.HANDLE_NIL);
    }

    private PublisherQos getPublisherQos(DomainParticipant participant, DdsQosConfiguration config) {
    	PublisherQos qos = new PublisherQos();
    	participant.get_default_publisher_qos(qos);
    	if (config != null && config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			DomainParticipantFactory.get_instance().get_publisher_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, getEndpoint().getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	return qos;
    }


    private DataWriterQos getDataWriterQos(DomainParticipant participant, DdsQosConfiguration config) {
    	DataWriterQos qos = new DataWriterQos();
    	participant.get_default_datawriter_qos(qos);
    	
    	if (config != null && config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			DomainParticipantFactory.get_instance().get_datawriter_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, getEndpoint().getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	    	
    	return qos;
    }
    
    public DataWriterProtocolStatus getProtocolStatus(){
    	DataWriterProtocolStatus status = null;
    	
    	if( writer != null ){
    		status = new DataWriterProtocolStatus();
    		writer.get_datawriter_protocol_status(status);
    	}
    	
    	return status;
    }
}
