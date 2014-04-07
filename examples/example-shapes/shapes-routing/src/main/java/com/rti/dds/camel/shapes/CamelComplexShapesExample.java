/**
 * Copyright 2013, Real-Time Innovations (RTI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.rti.dds.camel.shapes;

import java.util.Scanner;

import com.rti.dds.type.shape.ShapeType;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.rti.dds.camel.RtiComponent;

public class CamelComplexShapesExample {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addComponent("rti", new RtiComponent());
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("rti:/11/Square(com.rti.dds.type.ShapeType)/default")
                            .multicast()
                            .onPrepare(new Processor() {
                        public void process(Exchange exchange)
                                throws Exception {
                            ShapeType body = exchange.getIn().getBody(
                                    ShapeType.class);
                            ShapeType clone = new ShapeType(body);
                            exchange.getIn().setBody(clone);
                        }
                    })
                            .pipeline()
                            .process(new Processor() {
                        public void process(Exchange exchange)
                                throws Exception {
                            ShapeType st = exchange.getIn().getBody(
                                    ShapeType.class);
                            st.color = "RED";
                        }
                    })
                            .to("rti:/12/Circle(com.rti.dds.type.ShapeType)/default")
                            .end()
                            .pipeline()
                            .process(new Processor() {
                        public void process(Exchange exchange)
                                throws Exception {
                            ShapeType st = exchange.getIn().getBody(
                                    ShapeType.class);
                            st.shapesize = st.x / 2;
                            int temp = st.x;
                            st.x = st.y;
                            st.y = temp;

                        }
                    })
                            .to("rti:/12/Circle(com.rti.dds.type.ShapeType)/default")
                            .end()
                            .pipeline()
                            .convertBodyTo(String.class)
                            .to("file:shapes.data?fileName=shapes.txt&fileExist=Append")
                            .end().
                            end();
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

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
