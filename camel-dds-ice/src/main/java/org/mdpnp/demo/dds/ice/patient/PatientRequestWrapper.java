package org.mdpnp.demo.dds.ice.patient;

import com.rti.dds.type.ice.patient.PatientRequest;


public class PatientRequestWrapper {
    
    private PatientRequest patientRequest;
    
    public PatientRequestWrapper(PatientRequest patientRequest) {
        this.patientRequest = patientRequest;
    }

    public static PatientRequestWrapper create(PatientRequest patientRequest) {
        return new PatientRequestWrapper(patientRequest);
    }
    
    public PatientRequest getPatientRequest() {
        return patientRequest;
    }

    public void setPatientRequest(PatientRequest patientRequest) {
        this.patientRequest = patientRequest;
    }
    
    public String getCorrelationId() {
        return patientRequest.correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        patientRequest.correlationId = correlationId;
    }
    
   public String generateCorrelationId() {
       return PatientQueryUtil.generateCorrelationId();
   }
    
    public void initCorrelationId() {
        setCorrelationId(generateCorrelationId());
    }

    @Override
    public String toString() {
        return patientRequest.toString();
    }
}
