package org.mdpnp.demo.dds.ice.patient;

import com.rti.dds.type.ice.patient.PatientRequest;
import com.rti.dds.type.ice.patient.PatientResponse;

public class PatientQueryUtil {

   public static String getCorrelationId(PatientResponse patientResponse) {
        return patientResponse.correlationId;
    }

    public static String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

   public static void initCorrelationId(PatientResponse patientResponse) {
        patientResponse.correlationId = generateCorrelationId();
    }
    
   public static void initCorrelationId(PatientRequest patientRequest) {
        patientRequest.correlationId = generateCorrelationId();
    }
    
}
