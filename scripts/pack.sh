#!/bin/bash

JAR=samyGoChanEd.jar
DIR=dist
CLASS_DIR=out
DEST=$DIR/$JAR

rm -rf $DIR

mkdir $DIR

$JDK_HOME/bin/jar vcfm $DEST src/main/resources/META-INF/MANIFEST.MF -C $CLASS_DIR .
