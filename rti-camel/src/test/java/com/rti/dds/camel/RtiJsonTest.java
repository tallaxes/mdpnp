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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.StringWrapper;

import com.rti.dds.infrastructure.*;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;
import com.rti.dds.util.Enum;
import java.util.Arrays;
import java.io.ObjectStreamException;
import org.codehaus.jackson.annotate.JsonGetter;


public class RtiJsonTest extends CamelTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiJsonTest.class);
    private static final String DOMAIN = "25";

    @Test
    public void testJsonUnmarshal() throws Exception {
    	MockEndpoint result = getMockEndpoint("mock:result");
    	result.expectedMessageCount(1);
    	
    	template.sendBody("direct:start", "{\"id\":\"Orange.A\",\"value\":24.20920203008744,\"units\":{\"name\":\"CELSIUS\"}}");
    	
    	result.assertIsSatisfied();
    	assertEquals(TemperaturePojo.class, result.getExchanges().get(0).getIn().getBody().getClass());
    }
    

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    	return new RouteBuilder() {
            public void configure() {
                JacksonDataFormat jdf = new JacksonDataFormat(TemperaturePojo.class);
            	from("direct:start")
                	.unmarshal(jdf)
                	.to("mock:result");
             }
        };
    }
    
    @Override
    public boolean isCreateCamelContextPerClass(){
    	return true;
    }
}
