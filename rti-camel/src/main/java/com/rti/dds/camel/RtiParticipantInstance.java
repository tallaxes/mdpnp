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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Qos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.TypeSupportImpl;

import org.apache.camel.CamelException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.ClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO: doc.
 */
public class RtiParticipantInstance extends DdsReferencedInstance<DomainParticipant, DomainParticipantQos> {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiParticipantInstance.class);
	private DdsInstanceData ddsInstance;
    private RtiTopicHolder topics;
    private Map<String, TypeSupportImpl> registeredTypes = new HashMap<String, TypeSupportImpl>();
    
    public RtiParticipantInstance(final RtiParticipantHolder parent, final DdsInstanceData data) {
        super(parent);
        ddsInstance = data;
        topics = new RtiTopicHolder(this);

        // TODO: hardocoded hack for qos object, FIX IT
        // TODO: since wo do this in the constructor, the code below better not throw
        //  use a try/catch and handle this better, only set participant if the call succeeds.
        setQos((DomainParticipantQos)createQos(data.getDomainQos()));
        setValue(data.getFactory().create_participant(
            data.getDomain(),
            getQos(),
            null, // listener
            StatusKind.STATUS_MASK_NONE));
        LOG.info("created DomainParticipant on domain " + data.getDomain());
    }

    public RtiTopicHolder getTopics() {
        return topics;
    }

    public String getRegisteredType(String className, ClassResolver resolver) throws Exception {
        synchronized (registeredTypes) {
            for (final Map.Entry<String, TypeSupportImpl> entry : registeredTypes.entrySet()) {
                if (entry.getValue().getClass().getName().equals(className)) {
                    return entry.getKey();
                }
            }
            // if not found register it
            Class<?> type = resolver.resolveClass(className);
            if (type == null) {
                throw new CamelException("Cannot resolve class " + className);
            }
            Method getInstanceMethod = null;
            Method getTypeNameMethod = null;
            Method registerTypeMethod = null;
            try {
                getInstanceMethod = type.getMethod("get_instance");
                getTypeNameMethod = type.getMethod("get_type_name");
                registerTypeMethod = type.getMethod("register_type", DomainParticipant.class, String.class);
            } catch (Exception e) {
                throw new CamelException("Invalid TypeSupport class " + className);
            }
            TypeSupportImpl inst = (TypeSupportImpl)getInstanceMethod.invoke(null);
            String typeName = (String)getTypeNameMethod.invoke(null);
            if (typeName != null && inst != null) {
                registerTypeMethod.invoke(null, getValue(), typeName);
                registeredTypes.put(typeName, inst);
            }
            return typeName;
        }
    }

    public TypeSupportImpl getRegisteredTypeSupport(String typeName) {
        synchronized (registeredTypes) {
            return registeredTypes.get(typeName);
        }
    }

    @Override
    protected Qos createQos(DdsQosConfiguration config) {
    	DomainParticipantFactory factory = ddsInstance.getFactory();
		DomainParticipantQos qos = new DomainParticipantQos();
        factory.get_default_participant_qos(qos);
    	if (config.isCustom()) {
			try {
	    		if (config.getLibrary() != null && config.getProfile() != null) {
	    			factory.get_participant_qos_from_profile(qos, config.getLibrary(), config.getProfile());
	    		}
	    		RtiHelper.setQosPolicies(qos, config, ddsInstance.getCamelContext());
			} catch (Exception e) {
				throw new RuntimeCamelException("Cannot create qos instance from profile", e);
			}
    	}
    	return qos;
    }
}
