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

import java.util.Map;
import java.util.TreeMap;


/**
 * Provides configuration data for a RTI DomainParticipant(s) and Topic(s)
 */
public class DdsQosConfiguration {
	private String key;
	private String library;
	private String profile;
    private Map<String, String> params = new TreeMap<String, String>();

    public boolean isCustom() {
    	return library != null || profile != null || params.size() > 0;
    }

    public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParam(String param, String value) {
		this.params.put(param, value);
	}
}
