package org.mdpnp.vista.mock;

import java.util.List;
import org.mdpnp.vista.api.Patient;

public class PatientTestDataSet extends DataSetList<Patient> {
    
   public PatientTestDataSet(List<Patient> patientList) {
       super(patientList);
   }
    
}
