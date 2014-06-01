package org.mdpnp.vista.mock;

import java.util.List;
import java.util.Map;
import org.mdpnp.vista.api.Allergy;
import org.mdpnp.vista.api.MedicalRecord;
import org.mdpnp.vista.api.Patient;

public class MockEmr implements MedicalRecord {

    private List<Patient> patients;
    
    public MockEmr(List<Patient> patients) {
        this.patients = patients;
    }
    
    @Override
    public Patient getPatient(long id) {
        int index = new Long(id).intValue() - 1;
        return patients.get(index);
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
    
}
