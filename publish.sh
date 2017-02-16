#!/usr/bin/env bash

currentVersion="v"$(cat version.sbt | egrep -o '([0-9]|\.)+')

if [ "$1" == "--release" ]; then
    sbt -Dfinal release
else
    sbt release
    currentVersion=$currentVersion"-SNAPSHOT"
fi

git checkout repository &&
git reset --hard &&
git pull origin repository &&
rsync -av release/* mvn-repo/
git add mvn-repo/ &&
git commit -m $currentVersion &&
git push origin repository &&
git checkout master &&
sbt clean
rm -rf release