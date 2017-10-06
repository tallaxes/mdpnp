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

import org.apache.camel.Consumer;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipantFactory;

/**
 * Represents a RTI Endpoint.
 */
public class RtiEndpoint extends DefaultEndpoint implements DdsInstanceData {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiEndpoint.class);
	
    private RtiEndpointContext ddsContext;
    private String registeredType;
    private boolean useWaitSet;
    
    public RtiEndpoint(String uri, RtiComponent component) {
        super(uri, component);        
    }

    public Producer createProducer() throws Exception {
        return new RtiProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new RtiConsumer(this, processor);
    }
    
    public PollingConsumer createPollingConsumer() throws Exception {
    	return new RtiPollingConsumer(this);
    }

    public boolean isSingleton() {
        return true;
    }
    
    public boolean isLenientProperties() {
        return true;
    }

    public synchronized RtiEndpointContext createDdsContext() throws Exception {
        if (ddsContext != null) {
            // increment lock counters
            ddsContext.getParticipant().lock();
            // topic.lock
            return ddsContext;
        }
        RtiEndpointContext ddsc = new RtiEndpointContext(this);
        RtiParticipantInstance p = ((RtiComponent)getComponent()).getParticipants().getInstance(this, getParticipantKey());
        if (p != null) {
            p.lock();
            String className = ((RtiEndpointConfiguration)getEndpointConfiguration()).getTopicClassName();
            registeredType = p.getRegisteredType(RtiHelper.getTypeSupportFqn(className), getCamelContext().getClassResolver());
            LOG.debug("topicClassName: " + className + "; typeSupportFqn: " + RtiHelper.getTypeSupportFqn(className) + "; registeredType: " + registeredType);
            RtiTopicInstance t = p.getTopics().getInstance(this, getTopicKey());
            if (t != null) {
                t.lock();
                ddsc.setParticipant(p);
                ddsc.setTopic(t);
            } else {
                p.release();
            }
        }
        ddsContext = ddsc;
        return ddsContext;
    }
    
    public void releaseDdsContext() {
        // ddsContext cannot be null
        ddsContext.getTopic().release();
        ddsContext.getParticipant().release();
    }

    /*
     *  DdsInstanceData methods(non-Javadoc)
     * @see com.rti.dds.camel.DdsInstanceData#getFactory()
     */
    public DomainParticipantFactory getFactory() {
        return ((RtiComponent)getComponent()).getFactory();
    }

    public int getDomain() {
        return ((RtiEndpointConfiguration)getEndpointConfiguration()).getDomain();
    }

    public String getParticipantKey() {
    	return RtiHelper.getParticipantKey((RtiEndpointConfiguration)getEndpointConfiguration());
    }

    public String getTopicKey() {
    	return RtiHelper.getTopicKey((RtiEndpointConfiguration)getEndpointConfiguration());
    }

    public String getTopic() {
        return ((RtiEndpointConfiguration)getEndpointConfiguration()).getTopic();
    }

    public String getRegisteredType() {
        return registeredType;
    }

	@Override
	public String getClassName() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getTopicClassName();
	}

	@Override
	public DdsQosConfiguration getDomainQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getDomainQos();
	}

	@Override
	public DdsQosConfiguration getTopicQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getTopicQos();
	}

	@Override
	public DdsQosConfiguration getPublisherQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getPublisherQos();
	}

	@Override
	public DdsQosConfiguration getSubscriberQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getSubscriberQos();
	}

	@Override
	public DdsQosConfiguration getDataReaderQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getDataReaderQos();
	}

	@Override
	public DdsQosConfiguration getDataWriterQos() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getDataWriterQos();
	}
	
	public boolean isWaitSet() {
		return ((RtiEndpointConfiguration)getEndpointConfiguration()).getUseWaitSet();
	}

}
