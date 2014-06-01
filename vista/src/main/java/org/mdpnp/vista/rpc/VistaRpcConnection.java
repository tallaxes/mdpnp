/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mdpnp.vista.rpc;

import org.mdpnp.vista.spi.Transport;
import org.mdpnp.vista.spi.VistaConnectionBase;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import static org.osehra.vista.soa.rpc.util.commands.RpcCommandLibrary.literal;
import static org.osehra.vista.soa.rpc.util.commands.RpcCommandLibrary.request;
import static org.osehra.vista.soa.rpc.util.commands.VistaRpcCommands.vista;

public class VistaRpcConnection extends VistaConnectionBase<RpcRequest, RpcResponse> {

    private boolean connected = false;
    private String userid = "EDWARDOST1";  // TODO: externalize and encapsulate vista credentials
    private String password = "3DWARD0ST2#";
    private String ipAddress = "localhost";
    private String hostName = "vista.mdpnp.org";

    public VistaRpcConnection(Transport<RpcRequest, RpcResponse> transport) {
        super(transport);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean connect() {
        log.debug("connect");
        if (!connected) {
            send(vista().connect(ipAddress, hostName));
            send(vista().signonSetup());
            send(vista().login(userid, password));
            send(request().version("0").name("XWB CREATE CONTEXT").parameter(literal("-2")));
            send(request().version("0").name("XWB CREATE CONTEXT").parameter(literal("&E.!6N.H!%dC!6ca.-)")));
            send(request().version("0").name("ORQPT DEFAULT LIST SOURCE"));
            connected = true;
        } // TODO: catch error conditions and binding login failures
        return connected;
    }

    @Override
    public boolean disconnect() {
        log.debug("disconnect");
        if (connected) {
            send(vista().disconnect());
            connected = false;
        } // TODO: catch error conditions and binding login failures
        return connected;
    }

    public String getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
}
