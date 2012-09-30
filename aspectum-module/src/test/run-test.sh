#!/bin/bash -xe

HOME="$(readlink -f "$(dirname "$0")")"/../../..
cd $HOME

mvn clean install dependency:copy-dependencies

CP=aspectum-module/target/test-classes
CP+=:aspectum-core/target/aspectum-core-0.0.1.jar
CP+=:aspectum-module/target/aspectum-module-0.0.1.jar
CP+=:aspectum-api/target/aspectum-api-0.0.1.jar

AGENT=./aspectum-module/target/dependency/aspectjweaver-1.7.0.jar

java -javaagent:"$AGENT"                                    \
    -cp "$CP"                                               \
    -Daspectum.callback=aspectum.module.SimpleTraceCallback \
    aspectum.sampleapp.SampleApplication

