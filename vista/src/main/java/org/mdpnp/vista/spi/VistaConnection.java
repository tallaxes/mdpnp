package org.mdpnp.vista.spi;

public interface VistaConnection<Request, Reply> {

    boolean isConnected();
    
    boolean connect();
    
    boolean disconnect();
    
    Transport getTransport();
}
