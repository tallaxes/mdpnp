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

import java.io.InputStream;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointConfiguration;
import org.apache.camel.impl.DefaultComponent;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;

/**
 * Represents the component that manages {@link HelloWorldEndpoint}.
 */
public class RtiComponent extends DefaultComponent {
    private DomainParticipantFactory dpFactory;
    private RtiParticipantHolder participants = new RtiParticipantHolder();
    
    @Override
    protected void doStart() throws Exception {
    	// hack to avoid setting the System path for loading native libraries
    	
        dpFactory = DomainParticipantFactory.get_instance();

   	 // Lookup and setup QOS profiles
        InputStream is = getCamelContext().getClassResolver()
        	.loadResourceAsStream("META-INF/services/dds/USER_QOS_PROFILES.xml");
        if (is != null) {
            DomainParticipantFactoryQos factory_qos = new DomainParticipantFactoryQos();
            dpFactory.get_qos(factory_qos);
            factory_qos.profile.url_profile.clear();
            factory_qos.profile.string_profile.clear();
            factory_qos.profile.string_profile.add(new java.util.Scanner(is).useDelimiter("\\A").next());
            dpFactory.set_qos(factory_qos);	
        }

        super.doStart();
    }
    
    @Override
    protected boolean useIntrospectionOnEndpoint() {
        return false;
    }

    @Override
    public EndpointConfiguration createConfiguration(String uri) throws Exception {
        return new RtiEndpointConfiguration(getCamelContext(), uri);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new RtiEndpoint(uri, this);
    }

    public DomainParticipantFactory getFactory() {
        return dpFactory;
    }

    public RtiParticipantHolder getParticipants() {
        return participants;
    }
}
