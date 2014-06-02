package org.mdpnp.demo.dds.ice.patient;

import com.rti.dds.type.ice.patient.PatientResponse;


public class PatientResponseWrapper {
    
    private PatientResponse patientResponse;
    
    public PatientResponseWrapper(PatientResponse patientResponse) {
        this.patientResponse = patientResponse;
    }


    public static PatientResponseWrapper create(PatientResponse patientResponse) {
        return new PatientResponseWrapper(patientResponse);
    }    
    
    public PatientResponse getPatientResponse() {
        return patientResponse;
    }

    public void setPatientResponse(PatientResponse patientResponse) {
        this.patientResponse = patientResponse;
    }
    
    public String getCorrelationId() {
        return patientResponse.correlationId;
    }
    
    public PatientResponseWrapper setCorrelationId(String correlationId) {
        patientResponse.correlationId = correlationId;
        return this;
    }
    
   public String generateCorrelationId() {
       return PatientQueryUtil.generateCorrelationId();
   }
    
    public void initCorrelationId() {
        setCorrelationId(generateCorrelationId());
    }
    
    @Override
    public String toString() {
        return patientResponse.toString();
    }
}
