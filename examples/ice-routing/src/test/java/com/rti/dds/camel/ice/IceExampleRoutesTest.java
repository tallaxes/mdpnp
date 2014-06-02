package com.rti.dds.camel.ice;

import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.type.ice.ConnectionState;
import com.rti.dds.type.ice.ConnectionType;
import com.rti.dds.type.ice.DeviceConnectivity;
import com.rti.dds.type.ice.ValidTargets;
import java.util.Arrays;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:/META-INF/spring/ice-routing-spring.xml",
    "classpath:/com/rti/dds/camel/ice/IceExampleRoutes-context.xml"
})
public class IceExampleRoutesTest {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    
    @Produce(uri = "direct:deviceConnectivityTest.in", context = "iceExampleRoutesTestContext")
    private ProducerTemplate test;

    @EndpointInject(uri = "mock:deviceConnectivityTest.out", context = "iceExampleRoutesTestContext")
    private MockEndpoint result;
    
    public IceExampleRoutesTest() {
    }
    
    private DeviceConnectivity createDeviceConnectivity() {
        DeviceConnectivity message = (DeviceConnectivity) DeviceConnectivity.create();
        message.unique_device_identifier = "oPXRmr243lAgud9qXeZ98q9Q6YqmRrGrEVXJ";
        message.info = "";
        message.state = ConnectionState.Disconnected;
        message.type = ConnectionType.Simulated;
        ValidTargets validTargets = (ValidTargets) ValidTargets.create();
        validTargets.userData = new StringSeq(Arrays.asList("userData"));
        message.valid_targets = validTargets;
        return message;
    }

    @Test
    public void testDeviceConnectivity() throws Exception {
        log.info("testDeviceConnectivity");
        DeviceConnectivity message = createDeviceConnectivity();
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(message);
        test.sendBody(message);
        result.assertIsSatisfied();
    }

}