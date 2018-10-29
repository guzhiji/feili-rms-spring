#!/usr/bin/env bash

cd `dirname "$0"`
# JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn spring-boot:run -Drun.arguments="superuser,admin,feiliks"

