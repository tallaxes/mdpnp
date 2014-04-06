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

Camel RTI Shapes Example
========================

This example shows basic routing for the RTI Shapes Demo. 
This example will route data from Squares in domain 11 to Circles in domain 12.

You will need to compile and install this example first:
  mvn install

The output will be an OSGi ready bundle that can be deployed into your container
	of choice; the bundle is defined using Blueprint.

To run the example of routing from Squares in domain 11 to Circles in domain 12, 
in your container run:
	source karafSetup.sh

The above setup script installs all of the necessary bundles, including the shapes
routing logic.
  
The more complex routing, from Squares in domain 11 to:
	1) Circles in domain 12, where the color is changed to RED 
	2) Circles in domain 12, where the x & y are transposed, and the size is
			changed to be x/2 
	3) a file shapes.data/shapes.txt
	
Can also be run within an OSGi container.  The blueprint file needs to be edited
to include this more complex route. 

The generated bundle's info:
	com.rti.dds.camel/example-shapes/1.0-SNAPSHOT

The complex example depends on the following bundles:
		- camel core bundle 
		- nddsjava bundle (com.rti.dds/nddsjava/5.0.0)
		- rti camel bundle (com.rti.dds.camel/rti-camel/1.0-SNAPSHOT)
		- shapes type bundle (com.rti.dds.type/shapes/1.0-SNAPSHOT)
		
  
The idl for the data type used in the Shapes demo can be found in the project shapes-types.
	That project will generate an OSGi bundle for the type used by this example.  See
	the pom.xml for the dependency declaration.
 
The example can also be run via the command line (non-OSGi container).  To run the simple example,
first navigate to the shapes-routing directory.  Then type:
	mvn exec:java -PShapesExaxmple

To run the complex example
	mvn exec:java -PComplexShapesExample



