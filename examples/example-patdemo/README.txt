## ------------------------------------------------------------------------
# 
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
#
## ------------------------------------------------------------------------

Camel RTI Patdemo Example
========================

This example shows basic routing for the RTI Patdemo Demo. 
This example will route Patdemo data from Topics in domain 28 to Topics in domain 29.

You will need to compile and install this example first:
  mvn install

The output will be an OSGi ready bundle that can be deployed into your container
	of choice; the bundle is defined using Blueprint.

To run the example of routing in your container run:
	source karafSetup.sh

The above setup script installs all of the necessary bundles, including the ice routing logic.
  

The generated bundle's info:
	com.rti.dds.camel/example-patdemo/1.0-SNAPSHOT
		
  
The idl for the data type used in the Ice demo can be found in the project patdemo-type. This project will generate an OSGi bundle for the type used by this example.  See 	the pom.xml for the dependency declaration.
 
The example can also be run via the command line (non-OSGi container).  To run the simple example,
first navigate to the ice-routing directory.  Then type:
	mvn exec:java -PPatdemoExample




