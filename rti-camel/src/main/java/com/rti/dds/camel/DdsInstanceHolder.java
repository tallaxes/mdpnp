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


/**
 * The HelloWorld producer.
 */
public abstract class DdsInstanceHolder<T extends DdsReferencedInstance<?, ?>> {
    // Not a ConcurrentHashMap, although access needs to be synchronized, 
    //  because more needs synchronizing than just access to Map.
    private Map<String, T> insts = new HashMap<String, T>();
    
    public T getInstance(DdsInstanceData data, String key) {
        synchronized (insts) {
            T value = insts.get(key);
            if (value == null) {
                value = createInstance(data);
                if (value == null || value.getValue() == null) {
                    return null;
                }
                // TODO: decide if we want to auto-increment here
                value.setKey(key);
                insts.put(key, value);
            }
            return value;
        }
    }
    
    public T getInstanceNoCreate(String key) {
    	synchronized(insts){
    		T value = insts.get(key);
    		return value;
    	}
    }
    
    public void notifyRelease(String key) {
        synchronized (insts) {
            insts.remove(key);
        }
    }

    public abstract T createInstance(DdsInstanceData data);
}
