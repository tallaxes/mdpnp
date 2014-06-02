package org.mdpnp.demo.dds.ice.converter;

import com.rti.dds.type.ice.patient.PatIdType;
import com.rti.dds.type.ice.patient.PatMeasure;
import org.apache.camel.Converter;
import com.rti.dds.type.ice.patient.PatientDemographics;
import com.rti.dds.type.ice.patient.PatientGender;
import com.rti.dds.type.ice.patient.PatientIdentificationEntry;
import com.rti.dds.type.ice.patient.PatientIdentificationEntrySeq;
import com.rti.dds.type.ice.patient.PatientIdentificationList;
import com.rti.dds.type.ice.patient.PatientResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.mdpnp.demo.dds.ice.patient.PatientResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// convert idl generated objects to wrapper objects that include bean accessors
// which can then be used with ognl in Camel expressions
@Converter
public final class PatientConverter {

    static private final Logger log = LoggerFactory.getLogger(PatientConverter.class);

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

    @Converter
    public static com.rti.dds.type.ice.patient.PatientResponse toPatientResponse(org.mdpnp.vista.api.Patient emrPatient) {
        log.debug("convert emrPatient ==> ice::PatientResponse");
        PatientResponse patientResponse = (PatientResponse) PatientResponse.create();

        PatientDemographics patient;
        patient = (PatientDemographics) PatientDemographics.create();
        patientResponse.patient = patient;

        String emrName = emrPatient.getName();
        patient.name = emrName;
        patient.family_name = emrName.split(",")[0];
        patient.given_name = emrName.split(",")[1];
        patient.birth_name = emrName;
        Date dob = convertEmrDate(emrPatient.getDob());
        patient.chronological_age = createIceMeasure(getAge(dob));
        patient.context_id = 1;
        patient.date_of_birth = createIceDate((byte) (dob.getYear() / 100), (byte) (dob.getYear() % 100), (byte) (dob.getMonth() + 1), (byte) dob.getDate());
        patient.gender = convertGender(emrPatient.getSex());
        patient.patient_id = createIcePatientIdentificationList(emrPatient.getSsn());
        return patientResponse;
    }


    @Converter
    public static org.mdpnp.demo.dds.ice.patient.PatientResponseWrapper toPatientResponseWrapper(org.mdpnp.vista.api.Patient emrPatient) {
        log.debug("convert emrPatient ==> ice::PatientResponseWrapper");
        return new PatientResponseWrapper(toPatientResponse(emrPatient));
    }
    
    public static com.rti.dds.type.ice.Date convertVistaDate(String dob) {
        log.debug("convertVistaDate: " + dob);
        com.rti.dds.type.ice.Date dobDate = (com.rti.dds.type.ice.Date) com.rti.dds.type.ice.Date.create();
        if (dob.toLowerCase().equals("*SENSITIVE*")) {
            createIceDate((byte) 19, (byte) 67, (byte) 6, (byte) 10);
        } else {
// sample string 2670610 is for 1967 june 10
            dobDate.century = (byte) 19;
            dobDate.year = Byte.valueOf(dob.substring(1, 3));
            dobDate.month = Byte.valueOf(dob.substring(3, 5));
            dobDate.day = Byte.valueOf(dob.substring(5));
        }
//        dobDate.year = dob.
        return dobDate;
    }

    public static Date convertEmrDate(String dob) {
        log.debug("convertEmrDate: " + dob);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dob);
        } catch (ParseException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            log.error(sw.toString());
        }
        return date;
    }

    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }


    private static PatMeasure createMeasure(float value, short units) {
        PatMeasure measure = (PatMeasure) PatMeasure.create();
        measure.value = value;
        measure.m_unit = units;
        return measure;
    }

    private static PatMeasure createIceMeasure(float value) {
        return createMeasure(value, (short) 0);
    }

    private static com.rti.dds.type.ice.Date createIceDate(byte century, byte year, byte month, byte day) {
        com.rti.dds.type.ice.Date date = (com.rti.dds.type.ice.Date) com.rti.dds.type.ice.Date.create();
        date.year = year;
        date.month = month;
        date.day = day;
        return date;
    }

    private static PatientGender convertGender(String emrGender) {
        switch (emrGender) {
            case "male":
                return PatientGender.gender_male;
            case "female":
                return PatientGender.gender_female;
            case "unknown":
                return PatientGender.gender_unknown;
            case "unspecified":
                return PatientGender.gender_unspecified;
            default:
                return  PatientGender.gender_unknown;
        }
        
    }

    private static PatientIdentificationEntry createIcePatientIdentificationEntry(String ssn) {
        PatientIdentificationEntry idEntry = (PatientIdentificationEntry) PatientIdentificationEntry.create();
        idEntry.patient_id = ssn;
        idEntry.provenance = "SSN";
        idEntry.type = PatIdType.pid_national;
        idEntry.verified = true;
        return idEntry;
    }
 
    private static PatientIdentificationList createIcePatientIdentificationList(String ssn) {
        PatientIdentificationList patientIdList = (PatientIdentificationList) PatientIdentificationList.create();
        List patientIds = new ArrayList();
        patientIds.add(createIcePatientIdentificationEntry(ssn));
        patientIdList.userData = new PatientIdentificationEntrySeq(patientIds);
        return patientIdList;
    }
    
    
}
