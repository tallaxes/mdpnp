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

import java.util.HashMap;
import java.util.Map;


public class RtiUri {
	private int id = 0;
	private String topic;
	private String type;
	private boolean pubsub;
	private QoS domainQos;
	private QoS topicQos;
	private QoS pubsubQos;

	private RtiUri(int id) {
		this.id = id;
		domainQos = new QoS(this);
		topicQos = new QoS(this);
		pubsubQos = new QoS(this);
	}

	public static RtiUri domain(int id) {
		// TODO: throw if id is negative?
		return new RtiUri(id);
	}

	public QoS qos() {
		if (topic == null) {
			return domainQos;
		} else if (!pubsub) {
			// TODO: throw if topic/type is null?
			return topicQos;
		} else {
			return pubsubQos;
		}
	}

	public QoS qos(String library, String profile) {
		QoS qos = qos();
		qos.profile(library, profile);
		return qos;
	}


	public RtiUri topic(String topic) {
		this.topic = topic;
		return this;
	}

	public RtiUri type(String type) {
		this.type = type;
		return this;
	}

	public RtiUri pubsub() {
		pubsub = true;
		return this;
	}

	@Override
	public String toString() {
		String qos;
		StringBuilder sb = new StringBuilder("rti:/");
		sb.append(id);
		qos = domainQos.format();
		if (!qos.isEmpty()) {
			sb.append(';');
			sb.append(qos);
		}
		
		sb.append('/');
		sb.append(topic);
		sb.append('(');
		sb.append(type);
		sb.append(')');
		qos = topicQos.format();
		if (!qos.isEmpty()) {
			sb.append(';');
			sb.append(qos);
		}
		
		if (pubsub) {
			sb.append('/');
			// TODO: could check but it should be non empty
			sb.append(pubsubQos.format());
		}

		return sb.toString();
	}
	
	public class QoS {
		private RtiUri parent;
		private String profile;
		private Map<String, String> params = new HashMap<String, String>();

		private QoS(RtiUri parent) {
			this.parent = parent;
		}

		public void profile(String library, String profile) {
			this.profile = library + "." + profile;
		}

		public QoS set(String param, String value) {
			params.put(param, value);
			return this;
		}

		public QoS set(String param, int value) {
			return set(param, Integer.toString(value));
		}

		public RtiUri topic(String topic) {
			return parent.topic(topic);
		}

		public RtiUri pubsub() {
			return parent.pubsub();
		}

		public String format() {
			return RtiHelper.getQosDefinition(profile, params);
		}

		@Override
		public String toString() {
			// doesn't make sense by itself, 
			// it's the parent's String representation that's interesting
		    return parent.toString();	
		}
	}
}
