/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mdpnp.vista;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.junit.experimental.categories.Category;
import org.mdpnp.test.IntegrationTest;

@Category(IntegrationTest.class)
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:Patient-TestData.xml",
    "classpath:/org/mdpnp/vista/PatientFixture-context.xml",
    "classpath:/org/mdpnp/vista/mockEmrUT.xml",
    "classpath:/org/mdpnp/vista/mockEmrST.xml",
    "classpath:/org/mdpnp/vista/MedicalRecordTest-context.xml"
})
public class MedicalRecordST {

    public MedicalRecordST() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected CamelContext patientFixtureContext;

    @Test
    public void testGetPatient() throws InterruptedException {
        log.info("---- " + getClass().getName() + ":testGetPatient start ----");
        log.info("---- Asserting Endpoints ----");
        MockEndpoint.assertIsSatisfied(patientFixtureContext);
        log.info("---- Asserting Endpoints finished ----");
        log.info("---- " + getClass().getName() + ":testGetPatient finish ----");
    }

}