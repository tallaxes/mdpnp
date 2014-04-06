# script for karaf: installs and starts bundles for example

echo "Start installation ..."

echo
echo "NDDSJava"
install -s  mvn:com.rti.dds/nddsjava/5.0.0

echo "Shapes type support"
install -s  mvn:com.rti.dds.type/shapes/1.0-SNAPSHOT

echo "RTI-Camel component"
install -s  mvn:com.rti.dds.camel/rti-camel/1.0-SNAPSHOT

echo "Blueprint Shapes Example"
install -s  mvn:com.rti.dds.camel/example-blueprint/1.0-SNAPSHOT

echo
echo "... installation finished successfully."
