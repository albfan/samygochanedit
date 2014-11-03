#!/bin/bash

OUT=out

rm -rf $OUT
mkdir $OUT

JAVAC=javac

if [ -n "${JDK_HOME}" ]
then
	if [ -x "${JDK_HOME}/bin/javac" ]
	then
		JAVAC="${JDK_HOME}/bin/javac"
	fi
fi

if ! [ -x "$JAVAC" ]
then
    echo cannot find javac compiler
    exit  -1 
fi

$JAVAC -verbose -cp "${SWT_LIB:-/usr/share/java}/swt.jar"  src/main/java/gui/* src/main/java/samyedit/* -d $OUT
