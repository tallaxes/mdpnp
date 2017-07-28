Camel RTI Component 
====================

This project builds the Camel-RTI component.  

If running the unit tests, the RTI libraries need to be loaded dynamically:
        export LD_LIBRARY_PATH=$NDDSHOME/lib/${arch}
where arch will depend on the OS version.  

The only immediate dependencies are Camel and RTI DDS Java API (`nddsjava-<ver>.jar`). The latter requires RTI DDS platform libraries at runtime (in `PATH` on Windows, in `LD_LIBRARY_PATH` on Linux).

#### URI

Example (see [`RtiUriTest.java`](src/test/java/com/rti/dds/camel/RtiUriTest.java#L26) for more):

	rti:/0/mytopic(java.lang.String);default;foo=bar;foo1=bar1

Syntax: 

	rti:/<domain>        [;<QoS profile>][;<property>=<value>]... 
	    /<topic>(<type>) [;<QoS profile>][;<property>=<value>]...
	   [/(pub|sub)       [;<QoS profile>][;<property>=<value>]...]
	   [/(dr|dw)         [;<QoS profile>][;<property>=<value>]...]
	   [&waitSet=true|false]            

QoS profile name is either `default` or in the `<library>.<profile>` form. 

#### Notes

* [example-shapes](../examples/example-shapes) is a good example of composing routes programmatically ([`example-shapes/shapes-routing`](../examples/example-shapes/shapes-routing)) and of using [`com.rti.dds.camel:maven-dds-plugin`](../tools/maven-dds) to generate code from IDL ([`example-shapes/shapes-type`](../examples/example-shapes/shapes-type)). It also works well with [RTI Shapes Demo](https://www.rti.com/downloads/shapes-demo)!
* For examples of XML-based route configuration  try [this search](https://github.com/search?l=&q=from%28%22rti%3A%2F%22+repo%3AEdwardOst%2Fmdpnp+path%3Aexamples+language%3AXML&ref=advsearch&type=Code&utf8=%E2%9C%93). For more examples of composing routes programmatically, try [this search](https://github.com/search?l=&q=from%28%22rti%3A%2F%22+repo%3AEdwardOst%2Fmdpnp+path%3Aexamples+language%3AJava&ref=advsearch&type=Code&utf8=%E2%9C%93).
