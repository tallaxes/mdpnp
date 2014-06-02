package org.mdpnp.demo.dds.ice.converter;

import org.apache.camel.Converter;
import com.rti.dds.type.ice.patient.PatientDemographics;

// convert idl generated objects to wrapper objects that include bean accessors
// which can then be used with ognl in Camel expressions
@Converter
public final class PatientConverter {
 
    @Converter
    public static org.mdpnp.demo.dds.ice.patient.PatientRequestWrapper toPatientRequestWrapper(com.rti.dds.type.ice.patient.PatientRequest patientRequest) {
        return new org.mdpnp.demo.dds.ice.patient.PatientRequestWrapper(patientRequest);
    }

    @Converter
    public static com.rti.dds.type.ice.patient.PatientRequest toPatientRequest(org.mdpnp.demo.dds.ice.patient.PatientRequestWrapper patientRequestWrapper) {
        return patientRequestWrapper.getPatientRequest();
    }

    @Converter
    public static org.mdpnp.demo.dds.ice.patient.PatientResponseWrapper toPatientResponseWrapper(com.rti.dds.type.ice.patient.PatientResponse patientResponse) {
        return new org.mdpnp.demo.dds.ice.patient.PatientResponseWrapper(patientResponse);
    }

    @Converter
    public static com.rti.dds.type.ice.patient.PatientResponse toPatientResponse(org.mdpnp.demo.dds.ice.patient.PatientResponseWrapper patientResponseWrapper) {
        return patientResponseWrapper.getPatientResponse();
    }

//    @Converter
//    public static com.rti.dds.type.ice.PatientDemographics toIcePatientDemographic(org.osehra.vista.example.cxf.jaxrs.resources.Patient patient) {
//        PatientDemographics patientDemographics = (PatientDemographics) PatientDemographics.create();
//        patient.date_of_birth = convertDate(patient.getDob());
//        patient.getId();
//        parseName(patientDemographics, patient.getName());
//        int patientSex = sexToGender(patient.getSex());
//        patientDemographics.gender = PatientGender.valueOf(patientSex);
//        patient.getSsn();
//        patient.
//
//  PatDemoState pat_demo_state;
//  PatientClass patient_class;
//  PatientIdentificationList patient_id;
//  string<100> name;   // Unstructured name.
//  string<32> given_name;
//  string<32> family_name;
//  string<32> middle_name;
//  string<32> birth_name;  // Maiden name
//  string<32> suffix_name;
//  string<32> title_name;  // Example: Professor
//  PatientSex sex;
//  PatientGender gender;
//  PatientRace race;
//  PatientType patient_type;
//  Date date_of_birth;
//  PatMeasure chronological_age;  // Time elapsed since birth. For neonatal, Weeks
//
//  // Time elapsed between the first day of the last menstrual period and the
//  // day of delivery. For neonatal, e.g., in hours or in weeks. Units include
//  // Days, weeks, months, years
//  PatMeasure gestational_age;
//  PatMeasure patient_height;
//  PatMeasure patient_weight;        // for neo-natal
//  PatMeasure patient_birth_length;  // for neo-natal
//  PatMeasure patient_birth_weight;  // for neo-natal
//  PatMeasure patient_head_circumference;  // for neo-natal
//  PatientIdentificationEntry mother_patient_id;  // for neo-natal
//  string<100> mother_name;   // Unstructured operator name, for neo-natal
//        
//        return patientDemographics;
//    }

    // TODO: convert patientSex property to correct ordinal for 
    // PatientGender enum
    public static int sexToGender(String patientSex) {
        return 1;
    }

    public static void parseName(PatientDemographics patientDemographics, String name) {
        patientDemographics.given_name = name;
    }

    public static com.rti.dds.type.ice.Date convertDate(String dob) {
        com.rti.dds.type.ice.Date dobDate = (com.rti.dds.type.ice.Date) com.rti.dds.type.ice.Date.create();
        if (dob.toLowerCase().equals("*SENSITIVE*")) {
            dobDate.century = (byte) 19;
            dobDate.year = (byte) 67;
            dobDate.month = 6;
            dobDate.day = 10;
        }
//        dobDate.year = dob.
        return dobDate;
    }
}    
