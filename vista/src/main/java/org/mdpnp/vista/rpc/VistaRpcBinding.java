package org.mdpnp.vista.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mdpnp.vista.api.Allergy;
import org.mdpnp.vista.api.Patient;
import org.mdpnp.vista.VistaBindingBase;
import org.osehra.vista.soa.rpc.RpcRequest;
import org.osehra.vista.soa.rpc.RpcResponse;
import static org.osehra.vista.soa.rpc.util.commands.VistaRpcCommands.vista;
import static org.osehra.vista.soa.rpc.RpcResponse.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// provide a VistA RPC protocol binding for the MedicalRecord API
public class VistaRpcBinding extends VistaBindingBase<RpcRequest, RpcResponse> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private String userid = "EDWARDOST1";  // TODO: externalize and encapsulate vista credentials
    private String password = "3DWARD0ST2#";

    public VistaRpcBinding(VistaRpcConnection connection) {
        super(connection);
    }

    @Override
    public Map<Long, Patient> getPatients() {
        if (!isConnected()) {
            connect();
        }
        RpcResponse reply = send(vista().patientLookup());
        int count = reply.getContent().size() - 1;
        Map<Long, Patient> patients = new HashMap<>();
        for (int i = 0; i < count; i++) {
            long id = Long.parseLong(reply.getField(i, 0));
            Patient patient = new Patient(id, reply.getField(i, 1));
            patients.put(id, patient);
        }
        return patients;
    }

    @Override
    public Patient getPatient(long id) {
        if (!isConnected()) {
            connect();
        }

        RpcResponse reply = send(vista().patientLookup(id));
        if (log.isDebugEnabled()) { printPatient(id, reply); }
        
        Patient patient = new Patient(id, reply.getField(0, 0));
        patient.setSsn(RpcHelper.formatSsn(reply.getField(0, 3)));
        patient.setDob(String.format("%1$tY-%1$tm-%1$td", RpcHelper.parseDoB(reply.getField(0, 2))));
        patient.setSex(reply.getField(0, 1).equals("M") ? "male" : "female");
        return patient;
    }

    @Override
    public List<Allergy> getAllergies(long id) {
        if (!isConnected()) {
            connect();
        }
        RpcResponse reply = send(vista().allergies(id));

        List<Allergy> allergies = new ArrayList<>();

        int count = reply.getContent().size() - 1;
        for (int i = 0; i < count; i++) {
            Allergy a = new Allergy(id);
            a.setIndex(Long.parseLong(reply.getField(i, 0)));
            a.setAgent(reply.getField(i, 1));
            a.setSeverity(reply.getField(i, 2));
            a.setSymptoms(Arrays.asList(reply.getField(i, 3).split(";")));
            allergies.add(a);
        }
        return allergies;
    }

    // TODO: not clear how the patient info is passed
    @Override
    public boolean addAllergy(Allergy allergy) {
        if (!isConnected()) {
            connect();
        }
        Map<String, String> allergyParams = new HashMap<>();
        allergyParams.put("\"GMRAGNT\"", "POLLEN^9;GMRD(120.82,");
        allergyParams.put("\"GMRATYPE\"", "O^Other");
        allergyParams.put("\"GMRANATR\"", "A^Allergy");
        allergyParams.put("\"GMRAORIG\"", "51");
        allergyParams.put("\"GMRAORDT\"", "3140206.222");
        allergyParams.put("\"GMRASYMP\",0", "2");
        allergyParams.put("\"GMRASYMP\",1", "15^CONFUSION^^^");
        allergyParams.put("\"GMRASYMP\",2", "99^HYPOTENSION^^^");
        allergyParams.put("\"GMRACHT\",0", "1");
        allergyParams.put("\"GMRACHT\",1", "3140206.222049");
        allergyParams.put("\"GMRAOBHX\"", "o^OBSERVED");
        allergyParams.put("\"GMRARDT\"", "3140206");
        allergyParams.put("\"GMRASEVR\"", "3");
        allergyParams.put("\"GMRACMTS\",0", "1");
        allergyParams.put("\"GMRACMTS\",1", "Some comment");
        RpcResponse reply = send(vista().addAllergy(allergyParams));
        return true;  // TODO: use same semantics as Collections.add
    }

    private void printPatient(long id, RpcResponse reply) {
        log.debug("---------- printPatient [" + id + "] ----------");
        int lineCount = 0;
        for (Line line : reply.getContent()) {
            StringBuilder sb = new StringBuilder(lineCount + ": ");
            for (String item : line) {
                sb.append(item).append("|");
            }
            log.debug(sb.toString());
            lineCount += 1;
        }
        log.debug("---------- printPatient-end ----------");
    }
}
