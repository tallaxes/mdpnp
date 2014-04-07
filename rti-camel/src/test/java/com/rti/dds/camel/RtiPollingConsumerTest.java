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
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.StringWrapper;

public class RtiPollingConsumerTest extends CamelTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiPollingConsumerTest.class);
    private static final String DOMAIN = "25";

    @Test
    public void testKeyedInput() throws Exception {
    	MockEndpoint result = getMockEndpoint("mock:keyedResult");
    	
    	result.expectedMessageCount(1);

    	KeyedString ks = new KeyedString();
    	ks.key = "foo";
    	ks.value = "key value";
        template.sendBody(
        		"rti:/"+DOMAIN+"/TestKeyA(com.rti.dds.type.builtin.KeyedString)/dw;default;durability.kind=TRANSIENT_LOCAL_DURABILITY_QOS", 
        		ks);
    	template.sendBody("direct:keyedStart", "start");

    	result.assertIsSatisfied();
        KeyedString res = result.getExchanges().get(0).getIn().getBody(KeyedString.class);
        assertEquals(ks.key, res.key);
        assertEquals(ks.value, res.value);
    }
    
    @Test
    public void testKeyedInputNotPublished() throws Exception {
    	MockEndpoint result = getMockEndpoint("mock:keyNotPublishedResult");
    	
    	result.expectedMessageCount(1);

    	KeyedString ks = new KeyedString();
    	ks.key = "foo";
    	ks.value = "key value";
        template.sendBody(
        		"rti:/"+DOMAIN+"/TestKeyA(com.rti.dds.type.builtin.KeyedString)/dw;default;durability.kind=TRANSIENT_LOCAL_DURABILITY_QOS", 
        		ks);
    	template.sendBody("direct:keyNotPublishedStart", "start");

    	// sleep for 5s to let poll complete
    	try{
    		LOG.info("sleeping for 5s");
    		Thread.sleep(5000);
    	}catch(InterruptedException ie){
    		// don't care
    	}
    	
    	// result should only contain the "start" string
    	result.assertIsSatisfied();
    	Object res = result.getExchanges().get(0).getIn().getBody();
    	assertNull(res);
    }
    
    @Test
    public void testNokeyInput() throws Exception {
    	MockEndpoint result = getMockEndpoint("mock:nokeyResult");
    	
    	result.expectedMessageCount(1);
    	
    	String value = "nokey value";
    	template.sendBody(
    			"rti:/"+DOMAIN+"/TestNokey(java.lang.String)/dw;default;durability.kind=TRANSIENT_LOCAL_DURABILITY_QOS", 
    			value);
    	template.sendBody("direct:nokeyStart", "start");
    	
    	result.assertIsSatisfied();
    	StringWrapper res = result.getExchanges().get(0).getIn().getBody(StringWrapper.class);
    	assertEquals(value, res.value);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    	final String keyUri = "rti:/"+DOMAIN+"/TestKeyA(com.rti.dds.type.builtin.KeyedString)/dr;default;durability.kind=TRANSIENT_LOCAL_DURABILITY_QOS";
    	final String nokeyUri = "rti:/"+DOMAIN+"/TestNokey(java.lang.String)/dr;default;durability.kind=TRANSIENT_LOCAL_DURABILITY_QOS";
    	return new RouteBuilder() {
            public void configure() {
                from("direct:keyedStart")
                	.process(new Processor() {
	                	public void process(Exchange exchange) {
	                		RtiThreadLocalStorage.getThreadLocal().set("key=foo");
	                	}})
	                .pollEnrich(keyUri).to("mock:keyedResult"); 
                from("direct:keyNotPublishedStart")
                	.process(new Processor() {
	                	public void process(Exchange exchange) {
	                		RtiThreadLocalStorage.getThreadLocal().set("key=bar");
	                	}})                	
                	.pollEnrich(keyUri,1000).to("mock:keyNotPublishedResult");
                from("direct:nokeyStart")
                	.process(new Processor() {
	                	public void process(Exchange exchange) {
	                		RtiThreadLocalStorage.getThreadLocal().remove();
	                	}})                	
	                .pollEnrich(nokeyUri).to("mock:nokeyResult");
            }
        };
    }
    
    @Override
    public boolean isCreateCamelContextPerClass(){
    	return true;
    }
}
