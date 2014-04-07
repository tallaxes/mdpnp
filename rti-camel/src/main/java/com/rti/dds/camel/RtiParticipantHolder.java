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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.RETCODE_ERROR;


/**
 * RtiParticipantHolder.
 */
public class RtiParticipantHolder extends DdsInstanceHolder<RtiParticipantInstance> {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiParticipantHolder.class);
	
    public RtiParticipantInstance createInstance(DdsInstanceData data) {
        return new RtiParticipantInstance(this, data);
    }
    
    @Override
    public void notifyRelease(String key){
    	// get domain participant that has been released
    	DomainParticipant dp = getInstanceNoCreate(key).getValue();
    	
    	// let parent class process the release
    	super.notifyRelease(key);

    	// delete the participant
    	DomainParticipantFactory dpFactory = DomainParticipantFactory.get_instance();
    	try {
    		dp.delete_contained_entities();
    		dpFactory.delete_participant(dp);
    	}catch(RETCODE_ERROR err){
    		LOG.error("Unable to delete participant [" + key + "];" + err.getMessage());
    	}

    	LOG.info("Deleted participant [" + key + "]");
    }
}
