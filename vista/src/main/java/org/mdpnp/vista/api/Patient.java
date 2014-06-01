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

import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@XmlRootElement(name = "Patient")
public class Patient {
    private long id;
    private String ssn;
    private String name;
    private String sex;
    private String dob;

    private static final Logger log = LoggerFactory.getLogger(Patient.class);
    
    public Patient() {
    }
    
    public Patient(long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    public String getSsn() {
        return ssn;
    }
    
	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
   
    public String toString() {
        return "{\"Patient\"{" 
            + "\"id\":" + id 
            + ",\"name\":\"" + name 
            + "\",\"ssn\":\"" + ssn 
            + "\",\"dob\":\"" + dob 
            + "\",\"sex\":\"" + sex 
            + "\"}}";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        Patient otherPatient = (Patient) other;
        boolean result = id == otherPatient.id
           && name.equals(otherPatient.name)
           && ssn.equals(otherPatient.ssn)
           && dob.equals(otherPatient.dob)
           && sex.equals(otherPatient.sex);
        return result;
    }
        
}
