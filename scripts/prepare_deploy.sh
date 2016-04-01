#!/bin/bash
DEPLOY_DIR=./desktop/build/libs

mkdir -p $DEPLOY_DIR/core
cp -r core/assets $DEPLOY_DIR/core

cd $DEPLOY_DIR
mv *.jar Shooter.jar
zip -r Shooter.zip Shooter.jar core
