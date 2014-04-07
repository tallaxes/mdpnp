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

import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.URIField;
import org.apache.camel.impl.DefaultEndpointConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link HelloWorldEndpoint}.
 */
public class RtiEndpointConfiguration extends DefaultEndpointConfiguration {
    private static final transient Logger LOG = LoggerFactory.getLogger(RtiEndpointConfiguration.class);
	
	private final String KEY_PUBLISHER = "pub";
	private final String KEY_SUBSCRIBER = "sub";
	private final String KEY_DATA_READER = "dr";
	private final String KEY_DATA_WRITER = "dw";
	
    @URIField(component = "path")
    private String path;
    
    @URIField(component = "query")
    private String query;
    
    // parameter fields
    private boolean useWaitSet;
    
    // non-URI configuration fields
    private int domain;
    private String topic;
    private String topicClassName;
    private DdsQosConfiguration domainQos;
    private DdsQosConfiguration topicQos;
    private DdsQosConfiguration publisherQos;
    private DdsQosConfiguration subscriberQos;
    private DdsQosConfiguration dataReaderQos;
    private DdsQosConfiguration dataWriterQos;

    public RtiEndpointConfiguration(CamelContext camelContext) {
        super(camelContext);
    }

    public RtiEndpointConfiguration(CamelContext camelContext, String uri) {
        super(camelContext, uri);
    }

    protected void init() {
        domain = -1;
    }

    @Override
    protected void parseURI() {
        init();
        super.parseURI();
    }

    @Override
    public String toUriString(UriFormat format) {
        // TODO: improve this
        return "rti://" + "path";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        
        parsePath();
    }
    
    public String getQuery() {
    	return query;
    }
    
    public void setQuery(String query){
    	this.query = query;
    	
    	parseQuery();
    }

    public int getDomain() {
        return domain;
    }

    public String getTopic() {
        return topic;
    }
    
    public String getTopicClassName() {
        return topicClassName;
    }

    public DdsQosConfiguration getDomainQos() {
        return domainQos;
    }

    public DdsQosConfiguration getTopicQos() {
        return topicQos;
    }

    public DdsQosConfiguration getPublisherQos() {
        return publisherQos;
    }

    public DdsQosConfiguration getSubscriberQos() {
        return subscriberQos;
    }

    public DdsQosConfiguration getDataReaderQos() {
        return dataReaderQos;
    }

    public DdsQosConfiguration getDataWriterQos() {
        return dataWriterQos;
    }
    
    public void setUseWaitSet(boolean uws) {
    	this.useWaitSet = uws;
    }
    
    public boolean getUseWaitSet(){
    	return this.useWaitSet;
    }

    private void parsePath() {
        if (path != null) {
            String[] segments = path.split("/");
            checkCondition(segments.length >= 3 && segments.length <= 5 && "".equals(segments[0]), 
                "Invalid URI path for rti: endpoint (TODO: add description)", null);

            String pubsubKey = null;
            // ignore the first segment, it'll be an emtpy string, as the URI starts with a '/'
            for (int sidx = 1; sidx < segments.length; sidx++) {
            	DdsQosConfiguration qos = parseUriSegment(segments[sidx]);
            	switch (sidx) {
            	case 1:
            		// *domain* segment
                    try {
                        domain = Integer.parseInt(qos.getKey());
                        domainQos = qos;
                    } catch(NumberFormatException e) {
                        checkCondition(false, "Invalid domain id for rti: endpoint (" + qos.getKey() + ")", e);
                    }
            		break;

            	case 2:
            		// *topic* segment
                    topicQos = qos;
                    String tid = topicQos.getKey();
                    int sp = tid.lastIndexOf('(');
                    checkCondition(sp > 0 && (tid.charAt(tid.length() - 1) == ')'), "Invalid topic type definition", null);
                    topic = tid.substring(0, sp);
                    topicClassName = tid.substring(sp + 1, tid.length() - 1);
            		break;

            	default:
            		// *pubsub* segments
            		String key = qos.getKey();
            		if (KEY_PUBLISHER.equals(qos.getKey())) {
                        checkCondition(pubsubKey == null, "Incorrect order for pub/sub qos definition", null);
                        publisherQos = qos;
            		} else if (KEY_SUBSCRIBER.equals(qos.getKey())) {
                        checkCondition(pubsubKey == null, "Incorrect order for pub/sub qos definition", null);
                        subscriberQos = qos;
            		} else if (KEY_DATA_READER.equals(qos.getKey())) {
                        checkCondition(pubsubKey == null || KEY_SUBSCRIBER.equals(pubsubKey), "Incorrect order for reader/writer qos definition", null);
                        dataReaderQos = qos;
            		} else if (KEY_DATA_WRITER.equals(qos.getKey())) {
                        checkCondition(pubsubKey == null || KEY_PUBLISHER.equals(pubsubKey), "Incorrect order for reader/writer qos definition", null);
                        dataWriterQos = qos;
            		} 
            		pubsubKey = key;
          			break;
            	}
            }
        }
    }

    private DdsQosConfiguration parseUriSegment(String segment) {
    	boolean setId = true;
    	DdsQosConfiguration qos = new DdsQosConfiguration();
    	for (String part : segment.split(";")) {
            String[] kv = part.split("=");
            checkCondition(kv.length == 1 || kv.length == 2, "Invalid QoS key/value pair", null);
            if (kv.length == 1) {
            	if (setId) {
            		qos.setKey(kv[0]);
            		setId = false;
            	} else {
                    checkCondition(qos.getProfile() == null, "Multiple QoS profiles not allowed", null);
                    if (RtiHelper.DEFAULT_PROFILE.equals(part)) {
                        qos.setProfile(part);
                    } else {
                        String[] lp = part.split("\\.");
                        checkCondition(lp.length == 2 && lp[0].length() > 0 && lp[1].length() > 0, 
                            "QoS profile should of form 'library.profile'", null);
                        qos.setLibrary(lp[0]);
                        qos.setProfile(lp[1]);
                    }
            	}
    		} else {
                checkCondition(qos.getProfile() != null, "QoS profile must be specified before params", null);
                qos.setParam(kv[0], kv[1]);
    		}
    	}
    	return qos;
    }

    private void checkCondition(boolean condition, String exceptionMessage, Throwable cause) {
        if (!condition) {
            throw new RuntimeCamelException(exceptionMessage, cause);
        }
    }
    
    private void parseQuery(){
    	if( query == null ){
    		return;
    	}
    	
    	for(String part : query.split("&")){
    		String[] kv = part.split("=");
    		if( kv.length == 2){
    			// check if waitSet provided
    			if( kv[0].toLowerCase().equals("waitset") ){
    				setUseWaitSet(Boolean.parseBoolean(kv[1]));
    			}
    			
    			// don't care about other properties
    		}
    	}
    }
}
