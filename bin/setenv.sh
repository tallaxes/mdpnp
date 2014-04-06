#!/bin/sh
###############################################################################
##         (c) Copyright, Real-Time Innovations, All rights reserved.        ##
##                                                                           ##
##         Permission to modify and use for internal purposes granted.       ##
## This software is provided "as is", without warranty, express or implied.  ##
##                                                                           ##
###############################################################################

# This script requires a bit of work...
NDDSHOME=/opt/ndds.5.0.0
export NDDSHOME

LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${NDDSHOME}/lib/x64Linux2.6gcc4.4.5jdk
export LD_LIBRARY_PATH

