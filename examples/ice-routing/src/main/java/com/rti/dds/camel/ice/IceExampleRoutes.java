/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rti.dds.camel.ice;

import org.apache.camel.builder.RouteBuilder;

public class IceExampleRoutes extends RouteBuilder {

    @Override
    public void configure() {
        from("rti:/28/ice::DeviceConnectivity(com.rti.dds.type.ice.DeviceConnectivity)/default")
                .to("rti:/29/ice::DeviceConnectivity(com.rti.dds.type.ice.DeviceConnectivity)/default");
        from("rti:/28/ice::DeviceIdentity(com.rti.dds.type.ice.DeviceIdentity)/default")
                .to("rti:/29/ice::DeviceIdentity(com.rti.dds.type.ice.DeviceIdentity)/default");
        from("rti:/28/ice::InfusionStatus(com.rti.dds.type.ice.InfusionStatus)/default")
                .to("rti:/29/ice::InfusionStatus(com.rti.dds.type.ice.InfusionStatus)/default");
        from("rti:/28/ice::AlarmSettings(com.rti.dds.type.ice.AlarmSettings)/default")
                .to("rti:/29/ice::AlarmSettings(com.rti.dds.type.ice.AlarmSettings)/default");
        from("rti:/28/ice::LocalAlarmSettingsObjective(com.rti.dds.type.ice.LocalAlarmSettingsObjective)/default")
                .to("rti:/29/ice::LocalAlarmSettingsObjective(com.rti.dds.type.ice.LocalAlarmSettingsObjective)/default");
        from("rti:/28/ice::SampleArray(com.rti.dds.type.ice.SampleArray)/default")
                .to("rti:/29/ice::SampleArray(com.rti.dds.type.ice.SampleArray)/default");
        from("rti:/28/ice::Numeric(com.rti.dds.type.ice.Numeric)/default")
                .to("rti:/29/ice::Numeric(com.rti.dds.type.ice.Numeric)/default");
        from("rti:/28/ice::GlobalAlarmSettingsObjective(com.rti.dds.type.ice.GlobalAlarmSettingsObjective)/default")
                .to("rti:/29/ice::GlobalAlarmSettingsObjective(com.rti.dds.type.ice.GlobalAlarmSettingsObjective)/default");
        from("rti:/28/ice::Image(com.rti.dds.type.ice.Image)/default")
                .to("rti:/29/ice::Image(com.rti.dds.type.ice.Image)/default");
        from("rti:/28/ice::InfusionObjective(com.rti.dds.type.ice.InfusionObjective)/default")
                .to("rti:/29/ice::InfusionObjective(com.rti.dds.type.ice.InfusionObjective)/default");
        from("rti:/28/ice::Text(com.rti.dds.type.ice.Text)/default")
                .to("rti:/29/ice::Text(com.rti.dds.type.ice.Text)/default");
    }
}
