/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mdpnp.demo.dds.docbox.mock;

import com.rti.dds.type.ice.patient.PatientRequest;
import com.rti.dds.type.ice.patient.PatientResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.*;
import org.mdpnp.demo.dds.ice.patient.PatientFactory;
import org.mdpnp.test.IntegrationTest;
import org.junit.experimental.categories.Category;

// TODO: this test should use mockVistaIntegration.xml rather than rely on 
// a real vista instance.  Use mockVista server with rpc support

@Category(IntegrationTest.class)
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:container.xml",
    "classpath:testData.xml",
    "classpath:vista.xml",
    "classpath:vistaAdaptor.xml",
    "classpath:mockDocBox.xml",
    "classpath:/org/mdpnp/demo/dds/docbox/mock/mockDocBoxTest-context.xml"
})
public class MockDocBoxIT {

    public MockDocBoxIT() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private ApplicationContext springContext;
    @Autowired
    protected CamelContext mockDocBoxContext;
    @Autowired
    protected PatientFactory patientFactory;
    @Produce(uri = "direct:mockDocBoxTest.in", context = "mockDocBoxTestContext")
    private ProducerTemplate test;
    @EndpointInject(uri = "mock:mockDocBoxTest.out", context = "mockDocBoxTestContext")
    private MockEndpoint result;

    @Test
    public void testGetPatient() throws InterruptedException {
        Object systemProperties = springContext.getBean("systemPropertiesConfig");
        log.info((systemProperties == null) ? "systemProperties is null" : "found systemProperties");
        log.info("Test method: " + getClass().getName() + ".testGetPatient");
        PatientRequest request = patientFactory.createPatientRequest();
        PatientFactory factory = springContext.getBean("patientFactory", PatientFactory.class);
        PatientResponse expectedResponse = factory.createPatientResponse(request);

//      enable to verify assertions are working with negative case; test should fail
//        expectedResponse.patient.birth_name = "some junk";

        result.expectedMessageCount(1);
        result.expectedBodiesReceived(expectedResponse);
        PatientResponse response = test.requestBody(request, PatientResponse.class);
        result.assertIsSatisfied();
    }
}