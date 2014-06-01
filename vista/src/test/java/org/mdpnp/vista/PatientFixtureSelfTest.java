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

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:Patient-TestData.xml",
    "classpath:/org/mdpnp/vista/PatientFixture-context.xml",
    "classpath:/org/mdpnp/vista/PatientFixtureSelfTest-context.xml"
})
public class PatientFixtureSelfTest {

    public PatientFixtureSelfTest() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected CamelContext patientFixtureContext;

    @Test
    public void fixtureSelfTest() throws InterruptedException {
        log.info("fixtureSelfTest");
        MockEndpoint.assertIsSatisfied(patientFixtureContext);
    }
    
}