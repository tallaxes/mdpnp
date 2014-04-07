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

import junit.framework.Assert;

import org.apache.camel.TypeConversionException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class QosConfigTest extends CamelTestSupport {

    @Test
    public void testSetVisibleField() throws Exception {
    	SuperClass dummy = new SuperClass();
    	
    	Assert.assertEquals("camel", dummy.visible);
    	RtiHelper.setConfigurationField(context, dummy, "visible", "rti");
    	Assert.assertEquals("rti", dummy.visible);
    }

    @Test
    public void testSetNonVisibleField() throws Exception {
    	SuperClass dummy = new SuperClass();
    	
    	Assert.assertEquals("cxf", dummy.getInvisible());
    	try {
        	RtiHelper.setConfigurationField(context, dummy, "invisible", "talend");
        	Assert.fail("Cannot set non-public fields");
    	} catch (NoSuchFieldException e) {
    		// expected
    	}
    }

    @Test
    public void testSetVisibleSubclassField() throws Exception {
    	SubClass dummy = new SubClass();
    	
    	Assert.assertEquals("camel", dummy.visible);
    	RtiHelper.setConfigurationField(context, dummy, "visible", "rti");
    	Assert.assertEquals("rti", dummy.visible);
    }

    @Test
    public void testSetVisibleInnerField() throws Exception {
    	SubClass dummy = new SubClass();
    	
    	Assert.assertEquals("camel", dummy.outer.inner.stringValue);
    	RtiHelper.setConfigurationField(context, dummy, "outer.inner.stringValue", "rti");
    	Assert.assertEquals("rti", dummy.outer.inner.stringValue);
    }

    @Test
    public void testSetVisibleInnerPrimitiveField() throws Exception {
    	SubClass dummy = new SubClass();
    	
    	// NOTE: we pass a numeric value *as String* for a field of type "int"
    	Assert.assertEquals(2000, dummy.outer.inner.int_value);
    	RtiHelper.setConfigurationField(context, dummy, "outer.inner.int_value", "256");
    	Assert.assertEquals(256, dummy.outer.inner.int_value);
    }

    @Test
    public void testSetVisibleInnerFieldBadValue() throws Exception {
    	SubClass dummy = new SubClass();
    	
    	// NOTE: we pass a *non-numeric* value as String for a field of type "int"
    	Assert.assertEquals(2000, dummy.outer.inner.int_value);
    	try {
        	RtiHelper.setConfigurationField(context, dummy, "outer.inner.int_value", "non-integer");
    	} catch (TypeConversionException e) {
    		// expected
    	}
    }

    @Test
    public void testSetNonVisibleInnerField() throws Exception {
    	SuperClass dummy = new SuperClass();
    	
    	Assert.assertEquals(-1, dummy.outer.inner.getPrimitivePrivate());
    	try {
        	RtiHelper.setConfigurationField(context, dummy, "outer.inner.primitive_private", "256");
        	Assert.fail("Cannot set non-public fields");
    	} catch (NoSuchFieldException e) {
    		// expected
    	}
    }

    @Test
    public void testSetRtiEnumField() throws Exception {
    	EnumOuterClass dummy = new EnumOuterClass();

    	Assert.assertEquals(EnumClassKind.ENUM_CLASS_KIND_A, dummy.kind);
		RtiHelper.setConfigurationField(context, dummy, "kind", "ENUM_CLASS_KIND_B");
		Assert.assertEquals(EnumClassKind.ENUM_CLASS_KIND_B, dummy.kind);
    }
    
    @Test
    public void testSetRtiEnumFieldInvalidValue() throws Exception {
    	EnumOuterClass dummy = new EnumOuterClass();
    	
    	Assert.assertEquals(EnumClassKind.ENUM_CLASS_KIND_A, dummy.kind);
    	try{
    		RtiHelper.setConfigurationField(context,  dummy, "kind", "InvalidKind");
    		Assert.fail("Cannot set enumerated kind to non-instantiated kind");
    	}catch (NoSuchFieldException e) {
    		// expected
    	}
    }

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // we need the type converters
        startCamelContext();
    }

    public class InnerClass {
    	public String stringValue = "camel";
    	public int int_value = 2000;
    	private int primitive_private = -1;

    	public int getPrimitivePrivate() {
			return primitive_private;
		}
		public void setPrimitivePrivate(int primitivePrivate) {
			this.primitive_private = primitivePrivate;
		}
    }

    public class OuterClass {
    	public InnerClass inner = new InnerClass();
    	private InnerClass priv = new InnerClass();

		public InnerClass getPriv() {
			return priv;
		}
		public void setPriv(InnerClass priv) {
			this.priv = priv;
		}
    }

    public class SuperClass {
    	public OuterClass outer = new OuterClass();
    	public String visible = "camel";
    	protected String invisible = "cxf";

		public String getInvisible() {
			return invisible;
		}
    }

    public class SubClass extends SuperClass {
    }
    
    @SuppressWarnings("serial")
	public static class EnumClassKind extends com.rti.dds.util.Enum {

		protected EnumClassKind(String name, int ordinal) {
			super(name, ordinal);
		}
    	
    	public static final EnumClassKind ENUM_CLASS_KIND_A
			= new EnumClassKind("ENUM_CLASS_KIND_A", 0);

    	public static final EnumClassKind ENUM_CLASS_KIND_B
			= new EnumClassKind("ENUM_CLASS_KIND_B", 0);

    }
    
    public static class EnumOuterClass {
    	
    	public EnumClassKind kind = EnumClassKind.ENUM_CLASS_KIND_A;
    	public String visible = "rti";
    }
}
