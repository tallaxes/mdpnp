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
import org.junit.experimental.categories.Category;
import org.mdpnp.demo.dds.ice.patient.PatientFactory;
import org.mdpnp.test.SelfIntegrationTest;

@Category(SelfIntegrationTest.class)
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:testData.xml",
    "classpath:/META-INF/spring/vista.xml",
    "classpath:vistaAdaptor.xml",
    "classpath:mockDocBox.xml",
    "classpath:/org/mdpnp/demo/dds/docbox/mock/mockDocBoxTest-context.xml"
})
public class MockDocBoxCT {

    public MockDocBoxCT() {
    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext springContext;

    @Autowired
    protected CamelContext mockDocBoxContext;
    
    @Autowired
    protected PatientFactory patientFactory;

    @Produce(uri = "direct:in", context = "mockDocBoxTestContext")
    private ProducerTemplate test;

    @EndpointInject(uri = "mock:out", context = "mockDocBoxTestContext")
    private MockEndpoint result;

    @Test
    public void testGetPatient() throws InterruptedException {
        log.info("start: " + getClass().getName() + ".testGetPatient");
        PatientRequest request = patientFactory.createPatientRequest();
        PatientFactory factory = springContext.getBean("patientFactory", PatientFactory.class);
        PatientResponse expectedResponse = factory.createPatientResponse(request);

//      enable to verify assertions are working with negative case; test should fail
//        expectedResponse.patient.birth_name = "some junk";

        result.expectedMessageCount(1);
        result.expectedBodiesReceived(expectedResponse);
        PatientResponse response = test.requestBody(request, PatientResponse.class);
//        result.assertIsSatisfied();
        
        // sleep to highlight that the error condition  does not occur until shutdown
        log.info("end: " + getClass().getName() + ".testGetPatient");
//        Thread.sleep(5000);
    }

}