package org.mdpnp.vista;

import java.util.List;
import java.util.Map;
import org.mdpnp.vista.spi.Transport;
import org.mdpnp.vista.api.Allergy;
import org.mdpnp.vista.api.Patient;
import org.mdpnp.vista.spi.VistaConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class VistaBindingBase<Request, Reply> implements VistaBinding<Request, Reply> {

    private Transport<Request, Reply> transport;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private VistaConnection<Request, Reply> connection;
    
    public VistaBindingBase (VistaConnection<Request, Reply> connection) {
        this.connection = connection;
        this.transport = connection.getTransport();
    }

//    @PostConstruct
    public void init() {
        log.debug("init");
        connect();
    }

//    @PreDestroy
    public void destroy() {
        log.debug("destroy");
        disconnect();
    }
    
    @Override
    public Patient getPatient(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Long, Patient> getPatients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Allergy> getAllergies(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAllergy(Allergy allergy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected Reply send(Request request) {
        return transport.send(request);
    }

    protected Reply send(List<Request> requests) {
        return transport.send(requests);
    }

    protected boolean isConnected() {
        return connection.isConnected();
    }

    protected boolean connect() {
        return connection.connect();
    }

    protected boolean disconnect() {
        return connection.disconnect();
    }
    
}
