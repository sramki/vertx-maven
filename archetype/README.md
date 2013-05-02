Maven Archetype for Vertx 
=========================

This archetype creates an example Maven project for creating a module with Vert.x

# Building the archetype and deploying it remotely

    mvn clean deploy

# Building and installing in maven local

    mvn clean install

# Archetype parameters

Every project created by the archetype is defined by three parameters:

* `groupId` is the group ID you want to use for your project
* `artifactId` is the name of the module you want to create in your Vert.x project - the directory will be called this too
* `version` is the version of the module you want to create.

# Creating a project using the archetype

## The easiest way

`mvn archetype:generate -Dfilter=io.vertx:`

This will search for any archetypes in the `io.vertx` group and prompt you to choose one. The archetype will have to
be already in Maven Central or your local Maven repository for this to work.

You will also be prompted for the archetype parameters

## From oss.sonatype

`mvn archetype:generate -Dfilter=io.vertx: -DarchetypeCatalog=http://oss.sonatype.org/content/repositories/snapshots/archetype-catalog.xml`

Same as the easiest way but it will search in `oss.sonatype` for catalogs - this is useful if you want to use a
snapshot.

## Creating a project using the archetype and specifying parameters on the command line

This will search for any archetypes in the `io.vertx` group and prompt you to choose one.

`mvn archetype:generate -Dfilter=io.vertx: -DgroupId=com.foo.bar -DartifactId=baz2 -Dversion=1.0`

## Creating a project using the archetype and specifying it exactly and specifying parameters on the command line

This will search for any archetypes in the `io.vertx` group and prompt you to choose one.

   mvn archetype:generate  -DarchetypeGroupId=io.vertx  -DarchetypeArtifactId=vertx-maven-archetype -DarchetypeVersion=2.0 \
   -DgroupId=com.foo.bar -DartifactId=baz -Dversion=1.0

## Creating a project using the archetype from a local version and specifying parameters on the command line

This will search for any archetypes in the `io.vertx` group and prompt you to choose one.

   mvn archetype:generate  -DarchetypeRepository=file:///tmp/mvn-repo/ -DarchetypeGroupId=io.vertx \
   -DarchetypeArtifactId=vertx-maven-archetype -DarchetypeVersion=2.0 \
   -DgroupId=com.foo.bar -DartifactId=baz -Dversion=1.0

# Once your project is created

Let's assume you used `baz` as your artifactId

    cd baz

Create a project for your favourite IDE

    mvn eclipse:eclipse
    mvn idea:idea

Then do Maven stuff as normal

    mvn clean deploy
    mvn test

etc

# TODO.

 * See if lang parameter can be added, so that we can generate only language specific files.

