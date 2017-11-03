#!/bin/bash
set -e
set -x

cd ../
rm -rf /target/*/*.jar
if [ "$BRANCH_NAME" = 'master' ]; then
  git config remote.origin.fetch +refs/heads/*:refs/remotes/origin/*
  git config branch.$BRANCH_NAME.remote origin
  git config branch.$BRANCH_NAME.merge refs/heads/$BRANCH_NAME
  git config --global user.name "Jenkins"
  git config --global user.email "jenkins@signalvine.com"
  sbt "release with-defaults skip-tests" -Dfinal=true
else
  sbt package
fi
