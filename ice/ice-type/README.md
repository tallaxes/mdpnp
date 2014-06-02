## Camel RTI ICE Types

This module generates RTI type classes from IDL for basic ICE datatypes.
The generated classes are made available as a Karaf feature.

### Dependencies

Successfully installed RTI nddsjava OSGI bundles. 
 
### Build

    $ mvn install

### Deploy

This feature does not need to be installed because it will typically be installed
as a dependency for another feature such as ice-routing.  But if desired, it can
be independently installed as follows

     karaf> features:addurl mvn:com.rti.dds.type/ice-type/1.0-SNAPSHOT/xml/features
     karaf> features:install ice-type

### Known Bugs

There are two IDL files, common.idl and ice.idl.  common.idl is not directly
used by ice.idl.  Ideally it should be imported into other ice idl files.  Since
import is not supported in the RTI idl pre-processor, it is necessary to
manually copy and paste it into those files.  When this is done, only the
typedef statements should be copied.  The enum and struct statements must be
present in only the common.idl.