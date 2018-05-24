#!/usr/bin/env bash

cd `dirname "$0"`
mvn clean package
cp ./target/*.war /home/guzhiji/disk2/apache-tomcat-8.5.24/webapps

