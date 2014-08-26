#!/bin/bash

mkdir -p libs/

download() {
	version="$1"
	wget -O "libs/cauldron-${version}.jar" "http://files.minecraftforge.net/maven/net/minecraftforge/cauldron/${version}/cauldron-${version}-server.jar" || exit 1
}

execute() {
	version=$(xmllint --shell <(sed '1,2d' ${1}/pom.xml | sed '1i\<project>\') <<< "cat //versions.cauldron/text()" | grep "[0-9]")
	if [ -f libs/cauldron-${version}.jar ]; then
		echo "Library for $1 already downloaded, skipping."
		return
	fi

	download "$version"

	mvn install:install-file -Dfile=libs/cauldron-${version}.jar -DgroupId=net.minecraftforge -DartifactId=cauldron -Dversion=${version} -Dpackaging=jar
}

execute 1.6
execute 1.7

# version=$(xmllint --shell <(sed '1,2d' 1.6/pom.xml | sed '1i\<project>\') <<< "cat //versions.cauldron/text()" | grep "[0-9]")
# sed '1,2d' 1.6/pom.xml | sed '1i\<project>\'

