mvn -q clean package
cd target
unzip -qq *.zip
vertx runmod ${groupId}.${artifactId}-v${version}
