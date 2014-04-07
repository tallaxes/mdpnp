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

import java.io.Serializable;

import com.rti.dds.cdr.CdrHelper;
import com.rti.dds.infrastructure.Copyable;

public class TemperaturePojo implements Copyable, Serializable
{

    public String id = ""; /* maximum length = (255) */
    public double value;
    public UnitsEnumPojo units = (UnitsEnumPojo) UnitsEnumPojo.create();


    public TemperaturePojo() {

    }


    public TemperaturePojo(TemperaturePojo other) {

        this();
        copy_from(other);
    }



    public Object create() {
        return new TemperaturePojo();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        TemperaturePojo otherObj = (TemperaturePojo)o;



        if(!id.equals(otherObj.id)) {
            return false;
        }
            
        if(value != otherObj.value) {
            return false;
        }
            
        if(!units.equals(otherObj.units)) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += id.hashCode();
                
        __result += (int)value;
                
        __result += units.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>TemperatureTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

    	TemperaturePojo typedSrc = (TemperaturePojo) src;
    	TemperaturePojo typedDst = this;

        typedDst.id = typedSrc.id;
            
        typedDst.value = typedSrc.value;
            
        typedDst.units = (UnitsEnumPojo) typedDst.units.copy_from(typedSrc.units);
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("id: ").append(id).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("value: ").append(value).append("\n");
            
        strBuffer.append(units.toString("units ", indent+1));
            
        return strBuffer.toString();
    }
    
}
