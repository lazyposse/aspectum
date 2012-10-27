#!/bin/bash -xe

HOME="$(readlink -f "$(dirname "$0")")"/../../..
cd $HOME

mvn clean install dependency:copy-dependencies

CP=aspectum-module/target/test-classes
CP+=:aspectum-core/target/*
CP+=:aspectum-module/target/*
CP+=:aspectum-api/target/*

AGENT=./aspectum-module/target/dependency/aspectjweaver-1.7.0.jar

java -javaagent:"$AGENT"                                    \
    -cp "$CP"                                               \
    -Daspectum.callback=aspectum.module.SimpleTraceCallback \
    aspectum.sampleapp.SampleApplication
