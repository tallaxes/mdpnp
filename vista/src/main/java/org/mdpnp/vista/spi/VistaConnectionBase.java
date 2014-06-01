package org.mdpnp.vista.spi;

import org.mdpnp.vista.spi.VistaConnection;
import java.util.List;
import org.mdpnp.vista.spi.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VistaConnectionBase<Request, Reply> implements VistaConnection<Request, Reply> {

    private Transport<Request, Reply> transport;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public VistaConnectionBase(Transport<Request, Reply> transport) {
        this.transport = transport;
    }

    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean connect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected Reply send(Request request) {
        return transport.send(request);
    }

    protected Reply send(List<Request> requests) {
        return transport.send(requests);
    }

}
