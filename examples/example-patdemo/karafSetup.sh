# script for karaf: installs and starts bundles for Patdemo in clean container

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

echo "Patdemo Type support"
install -s  mvn:com.rti.dds.type/patdemo-type/1.0-SNAPSHOT

echo "RTI-Camel component"
install -s  mvn:com.rti.dds.camel/rti-camel/1.0-SNAPSHOT

echo "Patdemo Routing"
install -s  mvn:com.rti.dds.camel.patdemo/example-patdemo/1.0-SNAPSHOT

echo
echo "... installation finished successfully."
