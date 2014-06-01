package org.mdpnp.vista.spi;

import java.util.List;

public interface Transport<Request, Reply> {

    Reply send(Request request);

    Reply send(List<Request> requests);
    
}
