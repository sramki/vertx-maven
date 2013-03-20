vertx-maven-plugin
==================

Maven Plugin for running verticles in their own vert.x instance.

Install
-----
This version of the plugin is not yet available on Maven Central.

Group ID: org.vertx.build.maven

Artifact ID: vertx-maven-plugin

Current release version: 2.0.0-SNAPSHOT


Usage
-----

### vertx:runmod

Deploys the current module and starts the module. The module will continue to run until the plugin is explicitly stopped with CTRL-C.  
To start it, type:

	mvn vertx:runmod


### vertx:pullindeps

Pulls in all module dependencies. Can be hooked to a maven phase in the pom file or called directly from the command line.
To start it, type:

	mvn vertx:pullindeps


##Example configuration:

	<plugin>
		<groupId>org.vertx.build.maven</groupId>
		<artifactId>vertx-maven-plugin</artifactId>
		<version>2.0.0-SNAPSHOT</version>
		<configuration>
			<moduleName>${project.groupId}~${project.artifactId}~${project.version}</moduleName> <!-- default -->
			<vertxModulesDirectory>${basedir}/mods</vertxModulesDirectory> <!-- default -->
		</configuration>
	</plugin>  


Configuration Options
---------------------

	<configuration>
		<moduleName>${project.groupId}~${project.artifactId}~${project.version}</moduleName>
		<vertxHomeDirectory>/path/to/vertx2</vertxHomeDirectory>
		<vertxModulesDirectory>${basedir}/mods</vertxModulesDirectory>
		<classpath>file:///extra/entries</classpath>
		<configFile>/path/to/MyVerticle.conf</configFile>
		<instances>1</instances>
		<clusterHost>localhost</clusterHost>
		<clusterPort>25500</clusterPort>
	</configuration>

Notice that by default, the vertxModulesDirectory property is set to "${basedir}/mods".
* moduleName: The module to be executed.
* vertxHomeDirectory: The directory where vertx2 is installed (not required). Set it to make use of the mods directory under vert.x 2.0 home.
* vertxModulesDirectory: The directory where modules are to be installed. When set, this will be the mods directory to be used.
* classpath: Extra entries for the classpath. Notice that maven artifacts will be included automatically in the classpath.
* configFile: The config file to be used.
* instances: The number of verticle instances.
* clusterHost: The interface to be used for clustering.
* clusterPort: The port number to be used for clustering (defaults to 25500).
