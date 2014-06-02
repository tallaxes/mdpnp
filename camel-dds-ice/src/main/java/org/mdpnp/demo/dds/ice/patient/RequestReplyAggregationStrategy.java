package org.mdpnp.demo.dds.ice.patient;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReplyAggregationStrategy implements AggregationStrategy {

    private Class requestClass;
    private Class responseClass;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public RequestReplyAggregationStrategy(Class requestClass, Class responseClass) {
        this.requestClass = requestClass;
        this.responseClass = responseClass;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        log.debug("aggregate");
        log.debug(newExchange.getIn().getBody() == null ? "New exchange is null" : "Valid new exchange");
        assert (newExchange.getIn() != null);
        assert (newExchange.getIn().getBody() != null);
        Class bodyClass = newExchange.getIn().getBody().getClass();
        if (bodyClass != requestClass && bodyClass != responseClass) {
            String errorMessage = "Invalid message sent to aggregator: received " + bodyClass.getCanonicalName() + ", expected either " + requestClass.getCanonicalName() + " or " + responseClass.getCanonicalName();
            log.error(errorMessage);
            throw new UnsupportedOperationException(errorMessage);
        }
        
        if (oldExchange == null) {
            log.debug("initial request received");
            return newExchange;
        } else if (oldExchange.getIn().getBody().getClass() == responseClass) {
            log.warn("unexpected condition: response ({}) received before request ({})", responseClass.getCanonicalName(), requestClass.getCanonicalName());
            return oldExchange;
        } else if (newExchange.getIn().getBody().getClass() == responseClass) {
            log.debug("reply correlated with request");
            return newExchange;
        } else {
            assert false : "invalid state";
            throw new IllegalStateException("Unexpected message in RequestReplyAggregationStrategy");
        }
    }
}
