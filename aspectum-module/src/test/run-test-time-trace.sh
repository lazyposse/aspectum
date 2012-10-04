#!/bin/bash -xe

HOME="$(readlink -f "$(dirname "$0")")"/../../..
cd $HOME

ASPECTUM_VERSION=0.0.2

mvn clean install dependency:copy-dependencies

CP=aspectum-module/target/test-classes
CP+=:aspectum-core/target/aspectum-core-$ASPECTUM_VERSION.jar
CP+=:aspectum-module/target/aspectum-module-$ASPECTUM_VERSION.jar
CP+=:aspectum-api/target/aspectum-api-$ASPECTUM_VERSION.jar

AGENT=./aspectum-module/target/dependency/aspectjweaver-1.7.0.jar

java -javaagent:"$AGENT"                                    \
    -cp "$CP"                                               \
    -Daspectum.callback=aspectum.module.MethodTimeCallback  \
    aspectum.sampleapp.SampleApplication

