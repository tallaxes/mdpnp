mdpnp
=====

MDPNP contributions with apache technology


#### Building from source

- Install RTI Connext, set `NDDSHOME`, add license
- Point to libraries (on Windows, add `%NDDSHOME%\lib\<arch>` to `PATH`; on Linux, add `$NDDSHOME/lib/<arch>` to `LD_LIBRARY_PATH`)
	
- Clone and `mvn install` [EdwardOst/apache-platform](https://github.com/EdwardOst/apache-platform) - provides consistent versions for dependency management 
- Clone this repository
- OSGI-fy and install RTI DDS jars into local repo using [bin/jars2m2.sh](bin/jars2m2.sh) (On Windows 10 can use Windows Subsystem for Linux to run)
- Build top level project with Maven
