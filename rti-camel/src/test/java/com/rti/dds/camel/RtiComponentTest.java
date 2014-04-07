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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.rti.dds.type.builtin.StringWrapper;

public class RtiComponentTest extends CamelTestSupport {

    @Test
    public void testValidInput() throws Exception {

    	MockEndpoint result = getMockEndpoint("mock:result");
    	result.expectedMessageCount(1);

        template.sendBody("rti:/25;default;resource_limits.max_partitions=48/talend(java.lang.String)", "Hello World");

        assertMockEndpointsSatisfied();
        assertEquals(StringWrapper.class, result.getExchanges().get(0).getIn().getBody().getClass());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("rti:/25;default;resource_limits.max_partitions=48/talend(java.lang.String)").to("mock:result");
            }
        };
    }
}
