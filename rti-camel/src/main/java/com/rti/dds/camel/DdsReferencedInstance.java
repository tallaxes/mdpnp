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

import java.util.concurrent.atomic.AtomicInteger;

import com.rti.dds.infrastructure.Qos;


/**
 * The HelloWorld producer.
 */
public abstract class DdsReferencedInstance<V, Q> {
    protected AtomicInteger refs = new AtomicInteger(0);
    protected DdsInstanceHolder<? extends DdsReferencedInstance<V, Q>> parent;
    private String key;
    private V value;
    private Q qos;
    
    public DdsReferencedInstance(final DdsInstanceHolder<? extends DdsReferencedInstance<V, Q>> parent) {
        this.parent = parent;
    }

    public int lock() {
        return refs.incrementAndGet();
    }

    public void release() {
        int count = refs.decrementAndGet();
        if (count == 0) {
            parent.notifyRelease(getKey());
            // unusable instance
            parent = null;
            setKey(null);
            setValue(null);
            setQos(null);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Q getQos() {
        return qos;
    }

    public void setQos(Q qos) {
        this.qos = qos;
    }

    protected abstract Qos createQos(DdsQosConfiguration config);
}
