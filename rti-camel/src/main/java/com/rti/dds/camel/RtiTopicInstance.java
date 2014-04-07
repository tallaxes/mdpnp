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
import com.rti.dds.infrastructure.Qos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicQos;

import org.apache.camel.RuntimeCamelException;


/**
 * TODO: doc.
 */
public class RtiTopicInstance extends DdsReferencedInstance<Topic, TopicQos> {
	private DdsInstanceData ddsInstance;
	private DomainParticipant participant;

    public RtiTopicInstance(final RtiTopicHolder parent, final DdsInstanceData data) {
        super(parent);
        ddsInstance = data;

        participant = parent.getOwner().getValue();
        setQos((TopicQos)createQos(data.getTopicQos()));
        setValue(participant.create_topic(
            data.getTopic(),
            data.getRegisteredType(),
            getQos(),
            null, // listener
            StatusKind.STATUS_MASK_NONE));
    }

    @Override
    protected Qos createQos(DdsQosConfiguration config) {
    	DomainParticipantFactory factory = ddsInstance.getFactory();

    	TopicQos qos = new TopicQos();
    	participant.get_default_topic_qos(qos);
    	if (config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			factory.get_topic_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, ddsInstance.getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	return qos;
    }
}
