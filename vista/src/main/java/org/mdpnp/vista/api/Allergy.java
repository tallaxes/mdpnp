/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mdpnp.vista.api;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Allergy")
public class Allergy {
    private long id;
    private long index;
    private String agent;
    private String severity;
    private Collection<String> symptoms = new ArrayList<String>();
    
    public Allergy() {
    }
    
    public Allergy(long id) {
        this.id = id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    
    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }
    
    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgent() {
        return agent;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    public String getSeverity() {
        return severity;
    }
    
    public void setSymptoms(Collection<String> symptoms) {
    	this.symptoms = new ArrayList<String>(symptoms);
    }
	public Collection<String> getSymptoms() {
		return symptoms;
	}
   
    public String toString() {
        return "{\"Allergy\"{" 
            + "\"index\":" + index
            + ",\"agent\":\"" + agent 
            + "\",\"severity\":\"" + severity 
            + "\"}}";
    }

}
