#!/bin/bash -x

#                            Go to home dir
#                            ==============

HOME="$(readlink -f "$(dirname "$0")")"/..
cd "$HOME"

#                               Cleanup
#                               =======

find src -name '*.class' -exec rm {} \;

#                        Compile the sample app
#                        ======================

cd src/main/java
javac sample/Main.java sample/a/A.java sample/b/B.java
cd "$HOME"

set +x

#                      Running without the agent
#                      =========================

echo
echo "+-------------------------------------------------------------------------------"
echo "| Running WITHOUT the agent                                                     "
echo "+-------------------------------------------------------------------------------"
echo

java -cp src/main/java sample.Main

echo
echo "+-------------------------------------------------------------------------------"
echo "| Running WITH the agent                                                        "
echo "+-------------------------------------------------------------------------------"
echo

java -javaagent:target/dependency/aspectjweaver-1.7.0.jar                                                 \
     -cp src/main/java:src/main/resources:target/dependency/aspectjrt-1.7.0.jar:target/aspectum-agent.jar \
    sample.Main

#                               Cleanup
#                               =======

find src -name '*.class' -exec rm {} \;
