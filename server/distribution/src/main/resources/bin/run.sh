#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  SMSC Server Bootstrap Script                                         ##
##                                                                          ##
### ====================================================================== ###

DIRNAME=`dirname $0`
PROGNAME=`basename $0`
GREP="grep"

# Use the maximum available, or set MAX_FD != -1 to use that
MAX_FD="maximum"

#
# Helper to complain.
#
warn() {
    echo "${PROGNAME}: $*"
}

#
# Helper to puke.
#
die() {
    warn $*
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
linux=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;

    Darwin*)
        darwin=true
        ;;
        
    Linux)
        linux=true
        ;;
esac

# Read an optional running configuration file
if [ "x$RUN_CONF" = "x" ]; then
    RUN_CONF="$DIRNAME/run.conf"
fi
if [ -r "$RUN_CONF" ]; then
    . "$RUN_CONF"
fi

# Force IPv4 on Linux systems since IPv6 doesn't work correctly with jdk5 and lower
if [ "$linux" = "true" ]; then
   JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$SMSC_HOME" ] &&
        SMSC_HOME=`cygpath --unix "$SMSC_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup SMSC_HOME
if [ "x$SMSC_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    SMSC_HOME=`cd $DIRNAME/..; pwd`
fi
export SMSC_HOME

# Increase the maximum file descriptors if we can
if [ "$cygwin" = "false" ]; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ "$?" -eq 0 ]; then
        # Darwin does not allow RLIMIT_INFINITY on file soft limit
        if [ "$darwin" = "true" -a "$MAX_FD_LIMIT" = "unlimited" ]; then
            MAX_FD_LIMIT=`/usr/sbin/sysctl -n kern.maxfilesperproc`
        fi

	if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ]; then
	    # use the system max
	    MAX_FD="$MAX_FD_LIMIT"
	fi

	ulimit -n $MAX_FD
	if [ "$?" -ne 0 ]; then
	    warn "Could not set maximum file descriptor limit: $MAX_FD"
	fi
    else
	warn "Could not query system maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi

# Setup the classpath
runjar="$SMSC_HOME/bin/bootstrap.jar"
if [ ! -f "$runjar" ]; then
    die "Missing required file: $runjar"
fi
SP_BOOT_CLASSPATH="$runjar"

# Tomcat uses the JDT Compiler
# Only include tools.jar if someone wants to use the JDK instead.
# compatible distribution which JAVA_HOME points to
if [ "x$JAVAC_JAR" = "x" ]; then
    JAVAC_JAR_FILE="$JAVA_HOME/lib/tools.jar"
else
    JAVAC_JAR_FILE="$JAVAC_JAR"
fi
if [ ! -f "$JAVAC_JAR_FILE" ]; then
   # MacOSX does not have a seperate tools.jar
   if [ "$darwin" != "true" -a "x$JAVAC_JAR" != "x" ]; then
      warn "Missing file: JAVAC_JAR=$JAVAC_JAR"
      warn "Unexpected results may occur."
   fi
   JAVAC_JAR_FILE=
fi

if [ "x$SP_CLASSPATH" = "x" ]; then
    SP_CLASSPATH="$SP_BOOT_CLASSPATH"
else
    SP_CLASSPATH="$SP_CLASSPATH:$SP_BOOT_CLASSPATH"
fi
if [ "x$JAVAC_JAR_FILE" != "x" ]; then
    SP_CLASSPATH="$SP_CLASSPATH:$JAVAC_JAR_FILE"
fi

# Setup Service Platform specific properties
JAVA_OPTS="-Dprogram.name=$PROGNAME $JAVA_OPTS"

# Setup the java endorsed dirs
SP_ENDORSED_DIRS="$SMSC_HOME/lib/endorsed"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    SMSC_HOME=`cygpath --path --windows "$SMSC_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    SP_CLASSPATH=`cygpath --path --windows "$SP_CLASSPATH"`
    SP_ENDORSED_DIRS=`cygpath --path --windows "$SP_ENDORSED_DIRS"`
fi

# Display our environment
echo "========================================================================="
echo ""
echo "  SMSC Bootstrap Environment"
echo ""
echo "  SMSC_HOME    : $SMSC_HOME"
echo "  JAVA       : $JAVA"
echo "  JAVA_OPTS  : $JAVA_OPTS"
echo "  CLASSPATH  : $SP_CLASSPATH"
echo ""
echo "========================================================================="
echo ""

while true; do
   if [ "x$LAUNCH_SP_IN_BACKGROUND" = "x" ]; then
      # Execute the JVM in the foreground
      "$JAVA" $JAVA_OPTS \
         -Djava.endorsed.dirs="$SP_ENDORSED_DIRS" \
         -DSMSC_HOME="$SMSC_HOME" \
         -classpath "$SP_CLASSPATH" \
         com.ericsson.service.server.Main "$@"
      SP_STATUS=$?
   else
      # Execute the JVM in the background
      "$JAVA" $JAVA_OPTS \
         -Djava.endorsed.dirs="$SP_ENDORSED_DIRS" \
         -DSMSC_HOME="$SMSC_HOME" \
         -classpath "$SP_CLASSPATH" \
         com.ericsson.service.server.Main "$@" &
      SP_PID=$!
      
      # Trap common signals and relay them to the service portal process
      trap "kill -HUP  $SP_PID" HUP
      trap "kill -TERM $SP_PID" INT
      trap "kill -QUIT $SP_PID" QUIT
      trap "kill -PIPE $SP_PID" PIPE
      trap "kill -TERM $SP_PID" TERM
      
      # Wait until the background process exits
      WAIT_STATUS=128
      while [ "$WAIT_STATUS" -ge 128 ]; do
         wait $SP_PID 2>/dev/null
         WAIT_STATUS=$?
         if [ "${WAIT_STATUS}" -gt 128 ]; then
            SIGNAL=`expr ${WAIT_STATUS} - 128`
            SIGNAL_NAME=`kill -l ${SIGNAL}`
            echo "*** SP process (${SP__PID}) received ${SIGNAL_NAME} signal ***" >&2
         fi          
      done
      
      if [ "${WAIT_STATUS}" -lt 127 ]; then
         SP_STATUS=$WAIT_STATUS
      else
         SP_STATUS=0
      fi      
   fi
   
   if [ "$SP_STATUS" -eq 10 ]; then
      echo "Restarting Service Platform..."
   else
      exit $SP_STATUS
   fi
done

