Camel RTI Component 
====================

This project builds the Camel-RTI component.  

If running the unit tests, the RTI libraries need to be loaded dynamically:
        export LD_LIBRARY_PATH=$NDDSHOME/lib/${arch}
where arch will depend on the OS version.  

Note the bundle created by this project imports the package com.rti.dds.type.
The GAP Analysis document contains more information on why this import exists, and how
it relates to the use in the PoC. 
