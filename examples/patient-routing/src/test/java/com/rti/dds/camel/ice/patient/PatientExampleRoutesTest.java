/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rti.dds.camel.ice.patient;

import com.rti.dds.type.ice.patient.PatientRequest;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:/META-INF/spring/ice-patient-routing-spring.xml",
    "classpath:/com/rti/dds/camel/ice/patient/PatientExampleRoutes-context.xml"
})

public class PatientExampleRoutesTest {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    
    @Produce(uri = "direct:test.in", context = "patientExampleRoutesTestContext")
    private ProducerTemplate test;

    @EndpointInject(uri = "mock:test.out", context = "patientExampleRoutesTestContext")
    private MockEndpoint result;
    
    public PatientExampleRoutesTest() {
    }
    
    private PatientRequest createPatientRequest() {
        PatientFactory patientFactory = new PatientFactory();
        PatientRequest patientRequest = patientFactory.createPatientRequest();
        return patientRequest;
    }

    @Test
    public void testPatientRequest() throws Exception {
        Thread.sleep(2000);     // TODO: why is this sleep necessary?  without it the send message is not received
        log.info("testDeviceConnectivity");
        PatientRequest message = createPatientRequest();
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(message);
        test.sendBody(message);
        result.assertIsSatisfied();
    }

}