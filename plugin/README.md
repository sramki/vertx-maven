vertx-maven-plugin
==================

Maven Plugin for running verticles in their own vert.x instance.

Install
-----
This version of the plugin is not yet available on Maven Central.

Group ID: org.anacoders.plugins

Artifact ID: vertx-maven-plugin

Current release version: 2.0.0.0-SNAPSHOT


Versions
--------

This plugin's versions are aligned with vert.x versions with the minor version number to indicate increments of the plugin.
e.g. vert.x 2.0.0.FINAL would be 2.0.0.x

Usage
-----

### vertx:runMod

Executes the vertx:start goal in application mode (non-daemon). vert.x will continue to run until the plugin is explicitly stopped with CTRL-C.  
To start it, type:

	mvn vertx:run


### vertx:pullInDeps

Pulls in all module dependencies. Can be hooked to a maven phase in the pom file or called directly from the command line.
To start it, type:

	mvn vertx:pullInDeps


### vertx:start
This goal will start a verticle or vert.x module in it's own vert.x instance. By default this goal will be executed in daemon mode, meaning that it will not block the maven phase execution.
This goal is to be hooked to a maven phase.
To start it, type:

	mvn vertx:start
	
### vertx:stop

Executes the vertx:stop goal, used to cleanly stop vert.x.
This goal is to be hooked to a maven phase.
To start it, type:

	mvn vertx:start vertx:stop

### Hooking to Maven Phase
Add the following example plugin configuration to the pom.xml file:

	<plugin>
		<groupId>org.anacoders.plugins</groupId>
		<artifactId>vertx-maven-plugin</artifactId>
		<version>2.0.0.0-SNAPSHOT</version>
		<executions>
			<execution>
				<id>pull-in-dependencies</id>
				<phase>process-test-resources</phase>
				<goals>
					<goal>pullInDeps</goal>
				</goals>
			</execution>
			<execution>
				<id>start-vertx</id>
				<phase>pre-integration-test</phase>
				<goals>
					<goal>start</goal>
				</goals>
			</execution>
			<execution>
				<id>stop-vertx</id>
				<phase>post-integration-test</phase>
				<goals>
					<goal>stop</goal>
				</goals>
			</execution>
		</executions>
	</plugin>

Notice that by default, the vertxModulesDirectory property is set to "${basedir}/mods".

Example configuration:

	<plugin>
		<groupId>org.anacoders.plugins</groupId>
		<artifactId>vertx-maven-plugin</artifactId>
		<version>2.0.0.0-SNAPSHOT</version>
		<configuration>
			<moduleName>${project.groupId}~${project.artifactId}~${project.version}</moduleName> <!-- default -->
			<vertxModulesDirectory>${basedir}/mods</vertxModulesDirectory> <!-- default -->
		</configuration>
	</plugin>  


Configuration Options
---------------------

	<configuration>
		<daemon>true</daemon>
		<moduleName>${project.groupId}~${project.artifactId}~${project.version}</moduleName>
		<vertxHomeDirectory>/path/to/vertx2</vertxHomeDirectory>
		<vertxModulesDirectory>${basedir}/mods</vertxModulesDirectory>
		<classpath>file:///extra/entries</classpath>
		<configFile>/path/to/MyVerticle.conf</configFile>
		<instances>1</instances>
		<clusterHost>localhost</clusterHost>
		<clusterPort>25500</clusterPort>
	</configuration>

* daemon: Sets plugin execution mode. Only applicable to the "start" goal.
* moduleName: The module to be executed.
* vertxHomeDirectory: The directory where vertx2 is installed (not required). Set it to make use of the mods directory under vert.x 2.0 home.
* vertxModulesDirectory: The directory where modules are to be installed. When set, this will be the mods directory to be used.
* classpath: Extra entries for the classpath. Notice that maven artifacts will be included automatically in the classpath.
* configFile: The config file to be used.
* instances: The number of verticle instances.
* clusterHost: The interface to be used for clustering.
* clusterPort: The port number to be used for clustering (defaults to 25500).
