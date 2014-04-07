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

import java.io.ObjectStreamException;
import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonSetter;

import com.rti.dds.cdr.CdrHelper;
import com.rti.dds.util.Enum;

public  class UnitsEnumPojo extends Enum
{

    public static final UnitsEnumPojo CELSIUS = new UnitsEnumPojo("CELSIUS", 0);
    public static final int _CELSIUS = 0;
    
    public static final UnitsEnumPojo FAHRENHEIT = new UnitsEnumPojo("FAHRENHEIT", 1);
    public static final int _FAHRENHEIT = 1;
    
    public UnitsEnumPojo(){
    	super(valueOf(0).name(), valueOf(0).ordinal());
    }

    public static UnitsEnumPojo valueOf(int ordinal) {
        switch(ordinal) {
            
              case 0: return UnitsEnumPojo.CELSIUS;
            
              case 1: return UnitsEnumPojo.FAHRENHEIT;
            

        }
        return null;
    }

    public static UnitsEnumPojo from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];
        
        
        values[i] = CELSIUS.ordinal();
        i++;
        
        values[i] = FAHRENHEIT.ordinal();
        i++;
        

        Arrays.sort(values);
        return values;
    }

    public int value() {
        return super.ordinal();
    }

    @JsonGetter("name")
    public String getName() {
            return super.name();
    }
    @JsonSetter("name")
    public void setName(String newName) {
            // do nothing
    }
    
    
    /**
     * Create a default instance
     */  
    public static UnitsEnumPojo create() {
        

        return valueOf(0);
    }
    
    /**
     * Print Method
     */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);
            
        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }
        
        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private UnitsEnumPojo(String name, int ordinal) {
        super(name, ordinal);
    }
}
