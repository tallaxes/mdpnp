## Camel RTI ICE Patient Example

This example shows basic routing for the RTI ICE Patient Demo. 
It routes ICE Patient data from Topics in domain 28 to Topics in domain 29.

### Dependencies

The IDL for the ICE data types used in the ICE routing demo can be found in the sibling project _patient-type_. 
 
### Build

To build:

    $ mvn install

This will compile and run all unit tests.
The output will be an OSGi ready bundle and a supporting features file.

### Run

The example can be run with the 
[camel-maven-plugin](http://camel.apache.org/camel-maven-plugin.html)

    $ mvn camel:run

TODO: The bundle can be built to run as a stand-alone jvm using the JVM profile.

    mvn -Pjvm install

### Deploy

(NOTE: This step is not necessary if karaf is running on the build machine)
To deploy publish the bundle to nexus repository: 

* configure the karaf etc/org.ops4j.pax.url.mvn.cfg file to point at a valid nexus repository
* make sure that the maven .settings file uses the nexus repository
* deploy the generated bundles from the local maven repository to the nexus repository.

    $ mvn install
    $ mvn deploy

To deploy to a karaf instance:

    karaf> features:addurl mvn:com.rti.dds.camel.example/patient-routing/1.0-SNAPSHOT/xml/features
    karaf> features:install patient-routing

### Known Bugs

The unit tests succeed, but there is an error in the RTI shutdown that shows an exception.
This is not a blocker.  The unit tests still pass.

