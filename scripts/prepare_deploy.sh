#!/bin/bash
DEPLOY_DIR=./desktop/build/libs
ASSETS_DIR=assets

./gradlew desktop:dist

cp -r ./$ASSETS_DIR $DEPLOY_DIR/

git describe --tags > $DEPLOY_DIR/$ASSETS_DIR/version

cd $DEPLOY_DIR
mv *.jar Shooter.jar
zip -r Shooter.zip Shooter.jar assets
