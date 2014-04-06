#!/bin/sh
###############################################################################
##         (c) Copyright, Real-Time Innovations, All rights reserved.        ##
##                                                                           ##
##         Permission to modify and use for internal purposes granted.       ##
## This software is provided "as is", without warranty, express or implied.  ##
##                                                                           ##
###############################################################################

# This script requires a bit of work...
CURDIR=$PWD
PROJDIR=$(cd $(dirname "$0")/.. && pwd)
WORKDIR=/tmp/rti
VERSION=5.0.0
mkdir -p $WORKDIR

if [ -z "$NDDSHOME" ] ; then
   echo "NDDSHOME not set.  Please set the environment variable before running this script"
   exit
fi

cp $NDDSHOME/class/nddsjava.jar $WORKDIR
cp $NDDSHOME/class/rtiddsgen.jar $WORKDIR
cp $PROJDIR/bin/bnd-nddsjava-$VERSION.properties $WORKDIR

cd $WORKDIR
# Use bnd tools to convert jars to OSGi bundles
if [ ! -f bnd-0.0.384.jar ] ; then 
  wget http://central.maven.org/maven2/biz/aQute/bnd/0.0.384/bnd-0.0.384.jar
fi

echo OSGi-ify nddsjava.jar for version $VERSION
java -jar bnd-0.0.384.jar wrap -properties bnd-nddsjava-$VERSION.properties nddsjava.jar

mvn install:install-file -Dfile=./nddsjava.bar -DgroupId=com.rti.dds -DartifactId=nddsjava -Dversion=$VERSION -Dpackaging=jar
mvn install:install-file -Dfile=./rtiddsgen.jar -DgroupId=com.rti.dds -DartifactId=rtiddsgen -Dversion=$VERSION -Dpackaging=jar

cd $CURDIR
