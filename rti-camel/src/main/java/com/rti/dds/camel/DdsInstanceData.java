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

import org.apache.camel.CamelContext;

import com.rti.dds.domain.DomainParticipantFactory;


/**
 * Provides configuration data for a RTI DomainParticipant(s) and Topic(s)
 */
public interface DdsInstanceData {
    CamelContext getCamelContext();
    DomainParticipantFactory getFactory();

    int getDomain();
    String getTopic();
    String getRegisteredType();
    String getClassName();
    String getParticipantKey();
    String getTopicKey();

    DdsQosConfiguration getDomainQos();
    DdsQosConfiguration getTopicQos();
    DdsQosConfiguration getPublisherQos();
    DdsQosConfiguration getSubscriberQos();
    DdsQosConfiguration getDataReaderQos();
    DdsQosConfiguration getDataWriterQos();

}
