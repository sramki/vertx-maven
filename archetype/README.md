Maven Archetype for Vertx 
=========================

This archetype would help in setting up project structure for vertx module.


# Building the Archetype

  `mvn clean deploy`

# Creating a project using the Archetype

    mvn archetype:generate  -DarchetypeRepository=file:///tmp/mvn-repo/ \
        -DarchetypeGroupId=org.vertx.build  -DarchetypeArtifactId=vertx-maven-archetype -DarchetypeVersion=2.0

# A sample foo.bar project

    mvn archetype:generate  -DarchetypeRepository=file:///tmp/mvn-repo/ -DarchetypeGroupId=org.vertx.build  \
    -DarchetypeArtifactId=vertx-maven-archetype -DarchetypeVersion=2.0 \ 
    -DgroupId=com.foo.bar -DartifactId=baz -Dversion=1.0 -Dlang=Java

    cd baz;
    
    mvn clean compile test

# To dos.

 * See if lang parameter can be added, so that we can generate only language specific files.

