/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mdpnp.vista;

import java.util.List;
import javax.annotation.Resource;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdpnp.vista.api.MedicalRecord;
import org.mdpnp.vista.api.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.assertEquals;
        
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:Patient-TestData.xml",
    "classpath:/org/mdpnp/vista/mockEmrUT.xml",
})
public class MedicalRecordUT {

    public MedicalRecordUT() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource(name="patientTestData")
    private List<Patient> patientTestData;

    @Resource(name="mockEmr")
    private MedicalRecord emr;

    @Test
    public void testGetPatient() throws InterruptedException {
        log.info(getClass().getName() + ": testGetPatient");
        for (Patient testPatient : patientTestData) {
            Patient patientResponse = emr.getPatient(testPatient.getId());
            log.debug(patientResponse.toString());
            assertEquals("patient [" + testPatient.getId() + "]", testPatient, patientResponse);
        }
    }

}