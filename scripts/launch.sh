#!/bin/bash

JAR=samyGoChanEd.jar

JAVA=java

if [ -n "${JDK_HOME}" ]
then
	if [ -x "${JDK_HOME}/bin/java" ]
	then
		JAVA="${JDK_HOME}/bin/java"
	fi
fi

if ! [ -x "$JAVA" ]
then
    echo cannot find java executable
    exit  -1 
fi
$JAVA -cp dist/$JAR:${SWT_LIB:-/usr/share/java}/swt.jar gui.Main $@
