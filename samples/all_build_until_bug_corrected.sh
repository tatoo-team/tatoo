#!/bin/sh
for a in */build.xml ; do
	echo $(dirname $a)
	cd $(dirname $a)
	ant
	cd ..
done
