/**
 * Copyright 2013, Real-Time Innovations (RTI)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.rti.dds.camel.ice;

import java.util.Scanner;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;



public class CamelIceExample {
	private CamelIceExample() {
	}

	public static void main(String args[]) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("rti:/28/ice::DeviceConnectivity(com.rti.dds.type.ice.DeviceConnectivity)/default").to(
						"rti:/29/ice::DeviceConnectivity(com.rti.dds.type.ice.DeviceConnectivity)/default");
				from("rti:/28/ice::DeviceIdentity(com.rti.dds.type.ice.DeviceIdentity)/default").to(
						"rti:/29/ice::DeviceIdentity(com.rti.dds.type.ice.DeviceIdentity)/default");
				from("rti:/28/ice::InfusionStatus(com.rti.dds.type.ice.InfusionStatus)/default").to(
						"rti:/29/ice::InfusionStatus(com.rti.dds.type.ice.InfusionStatus)/default");
				from("rti:/28/ice::AlarmSettings(com.rti.dds.type.ice.AlarmSettings)/default").to(
					"rti:/29/ice::AlarmSettings(com.rti.dds.type.ice.AlarmSettings)/default");
				from("rti:/28/ice::LocalAlarmSettingsObjective(com.rti.dds.type.ice.LocalAlarmSettingsObjective)/default").to(
					"rti:/29/ice::LocalAlarmSettingsObjective(com.rti.dds.type.ice.LocalAlarmSettingsObjective)/default");
				from("rti:/28/ice::SampleArray(com.rti.dds.type.ice.SampleArray)/default").to(
					"rti:/29/ice::SampleArray(com.rti.dds.type.ice.SampleArray)/default");
				from("rti:/28/ice::Numeric(com.rti.dds.type.ice.Numeric)/default").to(
					"rti:/29/ice::Numeric(com.rti.dds.type.ice.Numeric)/default");
				from("rti:/28/ice::GlobalAlarmSettingsObjective(com.rti.dds.type.ice.GlobalAlarmSettingsObjective)/default").to(
					"rti:/29/ice::GlobalAlarmSettingsObjective(com.rti.dds.type.ice.GlobalAlarmSettingsObjective)/default");
				from("rti:/28/ice::Image(com.rti.dds.type.ice.Image)/default").to(
					"rti:/29/ice::Image(com.rti.dds.type.ice.Image)/default");
				from("rti:/28/ice::InfusionObjective(com.rti.dds.type.ice.InfusionObjective)/default").to(
					"rti:/29/ice::InfusionObjective(com.rti.dds.type.ice.InfusionObjective)/default");
				from("rti:/28/ice::Text(com.rti.dds.type.ice.Text)/default").to(
					"rti:/29/ice::Text(com.rti.dds.type.ice.Text)/default");
					
					
					
			}
		});
		context.start();

		// pause until user is done
		Scanner scan = new Scanner(System.in);
		System.out.println("Hit enter to shutdown");
		scan.nextLine().trim();

		// stop camel context
		context.stop();
		context.stop();
	}

}
