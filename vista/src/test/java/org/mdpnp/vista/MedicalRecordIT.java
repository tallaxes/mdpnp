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
import org.mdpnp.vista.rpc.VistaRpcConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.mdpnp.test.IntegrationTest;
import org.mdpnp.vista.rpc.VistaRpcBinding;

@Category(IntegrationTest.class)
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:Patient-TestData.xml",
    "classpath:/org/mdpnp/vista/PatientFixture-context.xml",
    "classpath:/org/mdpnp/vista/mockVistaIT.xml",
    "classpath:/org/mdpnp/vista/MedicalRecordTest-context.xml"
})
public class MedicalRecordIT {

    public MedicalRecordIT() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private VistaRpcConnection vistaConnection;
    
    // it would be preferable for this to be just the MedicalRecord API, but it
    // appears necessary to manually invoke the init method which is in the
    // subclass
    @Autowired
    private VistaRpcBinding emr;

    @Autowired
    protected CamelContext patientFixtureContext;

    @Test
    public void testGetPatient() throws InterruptedException {
        log.info("---- " + getClass().getName() + ":testGetPatient start ----");
        emr.init();
        assertTrue("failed to connect", vistaConnection.isConnected());
        
        log.info("---- Asserting Endpoints ----");
        MockEndpoint.assertIsSatisfied(patientFixtureContext);
        log.info("---- Asserting Endpoints finished ----");

        emr.destroy();
        assertFalse("failed to disconnect", vistaConnection.isConnected());

        log.info("---- " + getClass().getName() + ":testGetPatient finish ----");
    }

}