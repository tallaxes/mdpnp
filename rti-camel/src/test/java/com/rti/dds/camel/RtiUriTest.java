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

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RtiUriTest extends CamelTestSupport {

    @Test
    public void testValidUris() throws Exception {
    	final String[] uris = {
    		"rti:/0/talend(java.lang.String)",
    		"rti:/0/talend(java.lang.String)/",
    		"rti:/0;library.participant_profile/talend(java.lang.String)",
    		"rti:/0;default;foo=bar/talend(java.lang.String)",
    		"rti:/0;library.participant_profile;foo=bar;param=value/talend(java.lang.String)",
    		"rti:/0/talend(java.lang.String);library.topic_profile/",
    		"rti:/0/talend(java.lang.String);default;foo=bar",
    		"rti:/0/talend(java.lang.String);library.topic_profile;foo=bar;param=value/",
    		"rti:/0/talend(java.lang.String)/pub;library.pub/",
    		"rti:/0/talend(java.lang.String)/pub;default;foo=bar",
    		"rti:/0/talend(java.lang.String)/pub;library.pub;foo=bar;param=value/",
    		"rti:/0;library.participant_profile;a.b=0/talend(java.lang.String);default;foo=bar/pub;library.pub;x.y=1000",
    		"rti:/0/talend(java.lang.String)/sub;library.sub;foo=bar;param=value/",
    		"rti:/0;library.participant_profile;a.b=0/talend(java.lang.String);default;foo=bar/sub;library.sub;x.y=1000/dr;default;hello=world"
    	};
    	for (String uri : uris) {
        	RtiEndpointConfiguration config = getConfiguration(uri);
        	Assert.assertNotNull(config);
    	}

    	String uri = RtiUri
    		.domain(0).qos("library", "qos").set("foo", "bar")
    		.topic("topic").type("java.lang.String").qos("library", "profile").set("x", "y").set("a", "b")
    		.pubsub().qos().set("timeout", 0).toString();
    	log.info(uri);
    }

    @Test
    public void testIntegerDomainId() throws Exception {
    	try {
	    	getConfiguration("rti:/DOMAIN/talend(java.lang.String)");
	    	Assert.fail("Should not be able to parse URI with non numeric domain id");
    	} catch (Exception e) {
    		// ignore (we could check that we got the right message
    	}
    }

    @Test
    public void testMissingTopic() throws Exception {
    	try {
	    	getConfiguration("rti:/0/");
	    	Assert.fail("Should not be able to parse URI with missing topic");
    	} catch (Exception e) {
    		// ignore (we could check that we got the right message
    	}
    }

    @Test
    public void testMissingType() throws Exception {
    	try {
	    	getConfiguration("rti:/0/TOPIC");
	    	Assert.fail("Should not be able to parse URI with missing topic");
    	} catch (Exception e) {
    		// ignore (we could check that we got the right message
    	}
    }

    private RtiEndpointConfiguration getConfiguration(String uri) throws Exception {
    	RtiComponent rtic = context().getComponent("rti", RtiComponent.class);
    	return (RtiEndpointConfiguration)rtic.createConfiguration(uri);
    }

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }
}
