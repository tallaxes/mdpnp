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
import java.util.Map;

import org.apache.camel.CamelContext;

import com.rti.dds.infrastructure.Qos;


/**
 * The HelloWorld producer.
 */
public final class RtiHelper {
	public static final String DEFAULT_PROFILE = "default";

    private RtiHelper() {
        // Non instantiable utility class
    }

    public static String getParticipantKey(RtiEndpointConfiguration config) {
        String qos = formatQosProfileString(config.getDomainQos());
        return Integer.toString(config.getDomain()) + (qos.length() > 0 ? ";" + qos : "");
    }

    public static String getTopicKey(RtiEndpointConfiguration config) {
        String qos = formatQosProfileString(config.getTopicQos());
        // ignore the topic class, not relevant for a unique key
        return config.getTopic() + (qos.length() > 0 ? ";" + qos : "");
    }

    public static String formatQosProfileString(DdsQosConfiguration qos) {
        StringBuilder sb = new StringBuilder();
        if (qos.getLibrary() != null) {
            sb.append(qos.getLibrary());
            sb.append('.');
            sb.append(qos.getProfile());
        } else {
            sb.append("default");
        }
        // assume Map is already sorted (TreeMap)
        for (Map.Entry<String, String> kv : qos.getParams().entrySet()) {
            sb.append(';');
            sb.append(kv.getKey());
            sb.append('=');
            sb.append(kv.getValue());
        }
        return sb.toString();
    }
    
    public static String getTypeSupportFqn(final String className) {
    	String fqcn = className;
        final String[][] builtins = {
        	{"java.lang.String", "com.rti.dds.type.builtin.String"}
        };
        for (String[] mapping : builtins) {
	        if (mapping.length == 2 && className.equals(mapping[0])) {
	        	fqcn = mapping[1];
	            break;
	        }
        }
        return fqcn + "TypeSupport";
    }
    
    public static String getQosDefinition(String profile, Map<String, String> params) {
    	boolean defaultProfile = profile == null || profile.isEmpty();
		StringBuilder sb = new StringBuilder();
		sb.append((!defaultProfile || params.size() > 0) ? (defaultProfile ? DEFAULT_PROFILE : profile) : "");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(';');
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(entry.getValue());
		}
		return sb.toString();
    }
    
    public static void setConfigurationField(CamelContext camelContext, Object target, String name, String value) throws Exception {
    	int dot = name.indexOf('.');
		String f = dot > 0 ? name.substring(0, dot) : name;
		String r = dot > 0 ? name.substring(dot + 1) : null;
		
		Class<?> clazz = target.getClass();
		Field field = clazz.getField(f);
		if (r != null) {
			setConfigurationField(camelContext, field.get(target), r, value);
			return;
		}  
		
		if( com.rti.dds.util.Enum.class.isAssignableFrom(field.getType()) ){
			// an RTI enumerated QoS			
			// get class for "enumerated" kind
			Class<?> kindClazz = field.getType();
			// get static "enumerated" kind based on value parameter
            Object enumKind = kindClazz.getField(value).get(null);
            // set the value
            field.set(target, enumKind);
		}else{
			// not an RTI Enumerated QoS
			field.set(target, camelContext.getTypeConverter().convertTo(field.getType(), value));
		}
    }

    public static void setQosPolicies(Qos qos, DdsQosConfiguration config, CamelContext context) throws Exception {
		for (Map.Entry<String, String> entry : config.getParams().entrySet()) {
			setConfigurationField(context, qos, entry.getKey(), entry.getValue());
		}
    }
}
