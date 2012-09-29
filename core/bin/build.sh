#!/bin/bash -x

#                            Go to home dir
#                            ==============

HOME="$(readlink -f "$(dirname "$0")")"/..
cd "$HOME"

#                           Check JAVA_HOME
#                           ===============

if $JAVA_HOME
then
    set +x
    echo
    echo **ERROR** JAVA_HOME *must* be set !
    echo
    exit 1
fi

#                               Cleanup
#                               =======

#mvn clean

#                         Compile java classes
#                         ====================

#mvn package

#                           Get all the jars
#                           ================

mvn dependency:copy-dependencies

#                           Build the agent
#                           ===============

LIB="$HOME/target/dependency"
AJC_TO="$LIB/aspectjtools-1.7.0.jar"
AJC_RT="$LIB/aspectjrt-1.7.0.jar"
AJC_WE="$LIB/aspectjweaver-1.7.0.jar"

cd src/main/java

"$JAVA_HOME/bin/java" -classpath "$AJC_TO:$JAVA_HOME/lib/tools.jar:$CLASSPATH" \
    -Xmx64M                                                                    \
    org.aspectj.tools.ajc.Main -1.6 -cp $AJC_RT aspectum/Aspectum.java aspectum/Callback.java aspectum/CallbackImpl.java -outjar "$HOME/target/aspectum-agent.jar"
