/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rti.dds.camel.ice.patient;

import org.apache.camel.builder.RouteBuilder;

public class PatientExampleRoutes extends RouteBuilder {

    @Override
    public void configure() {
        log.info("----- Configuring PatientExampleRoutes -----");
        
        from("rti:/28/ice::PatientRequest(com.rti.dds.type.ice.patient.PatientRequest)/default")
         .to("rti:/29/ice::PatientRequest(com.rti.dds.type.ice.patient.PatientRequest)/defaultt");

        from("rti:/28/ice::PatientResponse(com.rti.dds.type.ice.patient.PatientResponse)/default")
         .to("rti:/29/ice::PatientResponse(com.rti.dds.type.ice.patient.PatientResponse)/defaultt");
    }
}
