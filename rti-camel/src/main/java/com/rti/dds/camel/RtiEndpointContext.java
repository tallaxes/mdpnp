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



/**
 * Represents the RTI equivalent of a Camel Endpoint
 * namely a DomainParticipant/Topic pair and the associated QoS 
 */
public class RtiEndpointContext {
    private RtiEndpoint endpoint;
    private RtiParticipantInstance participant;
    private RtiTopicInstance topic;

    public RtiEndpointContext(final RtiEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public RtiEndpoint getEndpoint() {
        return endpoint;
    }

    public RtiParticipantInstance getParticipant() {
        return participant;
    }

    public void setParticipant(RtiParticipantInstance participant) {
        this.participant = participant;
    }

    public RtiTopicInstance getTopic() {
        return topic;
    }

    public void setTopic(RtiTopicInstance topic) {
        this.topic = topic;
    }
}
