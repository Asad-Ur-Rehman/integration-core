#!/usr/bin/env bash

INTEGRATIONCOREVERSION=${1:-0.1.0}
BASEDIR=$(dirname "$0")

cp ${BASEDIR}/settings.xml ~/.m2/

mvn org.apache.maven.plugins:maven-dependency-plugin:2.9:get -Dartifact=com.signalvine:integration-core_2.11:$INTEGRATIONCOREVERSION:jar -Dtransitive=false -Dskip=true -DoverWriteSnapshots=true
mvn org.apache.maven.plugins:maven-dependency-plugin:2.9:get -Dartifact=com.signalvine:integration-core_2.11:$INTEGRATIONCOREVERSION:jar:sources -Dtransitive=false -Dskip=true -DoverWriteSnapshots=true
mvn org.apache.maven.plugins:maven-dependency-plugin:2.9:get -Dartifact=com.signalvine:integration-core_2.11:$INTEGRATIONCOREVERSION:pom -Dtransitive=false -Dskip=true -DoverWriteSnapshots=true
