# script for karaf: installs and starts bundles for ice in clean container

#
# THIS SCRIPT IS NO LONGER NEEDED SINCE A FEATURE FILE IS GENERATED
#

echo "Start installation ..."

echo 
echo "Camel 2.10.1"

features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.1/xml/features
features:install camel-core
features:install camel-blueprint
features:install camel-jms
features:install camel-jackson


echo
echo "NDDSJava"
install -s  mvn:com.rti.dds/nddsjava/5.0.0

echo "Ice Type support"
install -s  mvn:com.rti.dds.type/ice-type/1.0-SNAPSHOT

echo "RTI-Camel component"
install -s  mvn:com.rti.dds.camel/rti-camel/1.0-SNAPSHOT

echo "Ice Routing"
install -s  mvn:com.rti.dds.camel.ice/example-ice/1.0-SNAPSHOT

echo
echo "... installation finished successfully."
