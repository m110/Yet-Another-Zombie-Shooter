#!/bin/bash
DEPLOY_DIR=./desktop/build/libs

./gradlew desktop:dist

mkdir -p $DEPLOY_DIR/core
cp -r core/assets $DEPLOY_DIR/core

git describe --tags > $DEPLOY_DIR/core/assets/version

cd $DEPLOY_DIR
mv *.jar Shooter.jar
zip -r Shooter.zip Shooter.jar core
