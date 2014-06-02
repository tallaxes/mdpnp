package org.mdpnp.demo.dds.ice.patient;

import com.rti.dds.type.ice.patient.PatMeasure;
import com.rti.dds.type.ice.patient.PatientDemographics;
import com.rti.dds.type.ice.Date;
import com.rti.dds.type.ice.patient.PatDemoState;
import com.rti.dds.type.ice.patient.PatIdType;
import com.rti.dds.type.ice.patient.PatientClass;
import com.rti.dds.type.ice.patient.PatientGender;
import com.rti.dds.type.ice.patient.PatientIdentificationEntry;
import com.rti.dds.type.ice.patient.PatientIdentificationEntrySeq;
import com.rti.dds.type.ice.patient.PatientIdentificationList;
import com.rti.dds.type.ice.patient.PatientRace;
import com.rti.dds.type.ice.patient.PatientRequest;
import com.rti.dds.type.ice.patient.PatientResponse;
import com.rti.dds.type.ice.patient.PatientSex;
import com.rti.dds.type.ice.patient.PatientType;
import com.rti.dds.type.ice.patient.RaceType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private PatMeasure createMeasure(float value, short units) {
        PatMeasure measure = (PatMeasure) PatMeasure.create();
        measure.value = value;
        measure.m_unit = units;
        return measure;
    }

    private PatMeasure createIceMeasure(float value) {
        return createMeasure(value, (short) 0);
    }

    private Date createIceDate(byte century, byte year, byte month, byte day) {
        Date date = (Date) Date.create();
        date.year = year;
        date.month = month;
        date.day = day;
        return date;
    }
    
    private PatientRace createIcePatientRace() {
        PatientRace race = (PatientRace) PatientRace.create();
        race.provenance = "n/a";
        race.race_type = RaceType.race_other;
        return race;
    }
    
    private PatientIdentificationEntry createIcePatientIdentificationEntry() {
        PatientIdentificationEntry idEntry = (PatientIdentificationEntry) PatientIdentificationEntry.create();
        idEntry.patient_id = "123-45-6789";
        idEntry.provenance = "SSN";
        idEntry.type = PatIdType.pid_national;
        idEntry.verified = true;
        return idEntry;
    }
 
    private PatientIdentificationList createIcePatientIdentificationList() {
        PatientIdentificationList patientIdList = (PatientIdentificationList) PatientIdentificationList.create();
        List patientIds = new ArrayList();
        patientIds.add(createIcePatientIdentificationEntry());
        patientIdList.userData = new PatientIdentificationEntrySeq(patientIds);
        return patientIdList;
    }
    
    public PatientDemographics createPatient() {
        PatientDemographics patient;
        patient = (PatientDemographics) PatientDemographics.create();
        patient.birth_name = "Edward Ost";
        patient.chronological_age = createIceMeasure(47);
        patient.context_id = 1;
        patient.date_of_birth = createIceDate((byte) 19, (byte) 67, (byte) 6, (byte) 10);
        patient.family_name = "Ost";
        patient.gender = PatientGender.gender_male;
        patient.gestational_age = createIceMeasure(47);
        patient.given_name = "Edward";
        patient.handle = 0;
        patient.middle_name = "David";
        patient.mother_name = "Tran";
        patient.name =  "Edward Ost";
        patient.parent_handle = 0;
        patient.pat_demo_state = PatDemoState.discharged;
        patient.patient_birth_length = createIceMeasure(12);
        patient.patient_birth_weight = createIceMeasure(10);
        patient.patient_class = PatientClass.outpatient;
        patient.patient_head_circumference = createIceMeasure(12);
        patient.patient_height = createIceMeasure(68);
        patient.patient_id = createIcePatientIdentificationList();
        patient.patient_type = PatientType.adult;
        patient.patient_weight = createIceMeasure(200);
        patient.race = createIcePatientRace();
        patient.sex = PatientSex.sex_male;
        patient.suffix_name = "";
        patient.title_name = "";
        return patient;
    }

    public PatientRequest createPatientRequest() {
        PatientRequest patientRequest = (PatientRequest) PatientRequest.create();
        patientRequest.correlationId = PatientQueryUtil.generateCorrelationId();
        log.debug("createPatientRequest: correlationId: {}", patientRequest.correlationId);
        return patientRequest;
    }
    
    public PatientRequest createPatientRequest(PatientIdentificationList patient_id) {
        PatientRequest patientRequest = createPatientRequest();
        patientRequest.patient_id.copy_from(patient_id);
        return patientRequest;
    }
    
    public PatientResponse createPatientResponse(PatientRequest patientRequest) {
        PatientResponse patientResponse = (PatientResponse) PatientResponse.create();
        patientResponse.correlationId = patientRequest.correlationId;
        patientResponse.patient = createPatient();
        return patientResponse;
    }
    
}
