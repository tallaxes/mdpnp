package org.mdpnp.vista.transport.camel;

import org.mdpnp.vista.spi.Transport;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;

public class CamelTransport<Request, Reply> implements Transport<Request, Reply> {

    final private ProducerTemplate producer;
    final private Class<Request> requestClass;
    final private Class<Reply> replyClass;

    public CamelTransport(CamelContext camelContext, String uri) {
        this.requestClass = getRequestClass();
        replyClass = getReplyClass();
        producer = camelContext.createProducerTemplate();
        producer.setDefaultEndpoint(camelContext.getEndpoint(uri));
    }

    public CamelTransport(Endpoint vistaEndpoint) {
        this.requestClass = getRequestClass();
        replyClass = getReplyClass();
        producer = vistaEndpoint.getCamelContext().createProducerTemplate();
        producer.setDefaultEndpoint(vistaEndpoint);
    }

    private Class getRequestClass(Request... param) {
        return param.getClass().getComponentType();
    }

    private Class getReplyClass(Reply... param) {
        return param.getClass().getComponentType();
    }

    @Override
    public Reply send(Request request) {
        return producer.requestBody(request, replyClass);
    }

    @Override
    public Reply send(List<Request> requests) {
        Reply reply = null;
        for (Request request : requests) {
            reply = send(request);
        }
        return reply;
    }
}
