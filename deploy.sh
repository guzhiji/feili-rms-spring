#!/usr/bin/env bash

cd `dirname "$0"`
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn clean package
cp ./target/*.war /home/guzhiji/disk2/apache-tomcat-8.5.24/webapps

