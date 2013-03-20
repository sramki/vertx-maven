package org.vertx.maven.plugin.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static java.nio.file.Files.readAllBytes;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.vertx.java.core.json.JsonObject;

public abstract class BaseVertxMojo extends AbstractMojo {

    protected static final String CP_SEPARATOR = System.getProperty("os.name")
            .startsWith("Windows") ? ";" : ":";

    protected static final String VERTX_INSTALL_SYSTEM_PROPERTY = "vertx.install";

    protected static final String VERTX_MODS_SYSTEM_PROPERTY = "vertx.mods";

    protected static final String VERTX_RUNMOD_COMMAND = "runmod";
    protected List<String> args;

    /**
     * The Maven project.
     * 
     */
    @Component
    protected MavenProject mavenProject;

    /**
     * The name of the module to run.
     * 
     * If you're running a module, it's the name of the module to be run.
     * 
     */
    @Parameter(property = "run.moduleName", defaultValue = "${project.groupId}~${project.artifactId}~${project.version}")
    protected String moduleName;

    /**
     * <p>
     * Determines whether or not the server blocks when started. The default
     * behaviour (daemon = false) will cause the server to pause other processes
     * while it continues to run the verticle. This is useful when starting the
     * server with the intent to work with it interactively.
     * </p>
     * <p>
     * Often, it is desirable to let the server start and continue running
     * subsequent processes in an automated build environment. This can be
     * facilitated by setting daemon to true.
     * </p>
     * 
     */
    @Parameter(property = "run.daemon", defaultValue = "true")
    protected boolean daemon;

    /**
     * <p>
     * The config file for this verticle.
     * </p>
     * <p>
     * If the path is relative (does not start with / or a drive letter like
     * C:), the path is relative to the directory containing the POM.
     * </p>
     * <p>
     * An example value would be src/main/resources/com/acme/MyVerticle.conf
     * </p>
     * 
     */
    @Parameter(property = "run.configFile")
    protected File configFile;

    /**
     * The number of instances of the verticle to instantiate in the vert.x
     * server. The default is 1.
     * 
     */
    @Parameter(property = "run.instances", defaultValue = "1")
    protected Integer instances;

    /**
     * <p>
     * The path on which to search for the main and any other resources used by
     * the verticle.
     * </p>
     * <p>
     * If your verticle references other scripts, classes or other resources
     * (e.g. jar files) then make sure these are on this path. The path can
     * contain multiple path entries separated by : (colon).
     * </p>
     * 
     */
    @Parameter(property = "run.classpath")
    protected String classpath;

    /**
     * <p>
     * The home directory of your vert.x installation i.e. where you unzipped
     * the vert.x distro. For example C:/vert.x/vert.x-1.0.1.final
     * </p>
     * <p>
     * You will need to set this configuration option if you want to run any
     * out-of-the box modules like web-server.
     * </p>
     * 
     */
    @Parameter(property = "run.vertxHomeDirectory")
    protected String vertxHomeDirectory;

    /**
     * <p>
     * The home directory of your project modules i.e. target/mods
     * </p>
     * <p>
     * You will need to set this configuration option if you want to run any
     * out-of-the box modules like web-server.
     * </p>
     * 
     */
    @Parameter(property = "run.vertxModulesDirectory", defaultValue = "${basedir}/mods")
    protected String vertxModulesDirectory;

    public List<String> getArgs() throws MojoExecutionException {
        if (vertxHomeDirectory != null) {
            System.setProperty(VERTX_INSTALL_SYSTEM_PROPERTY,
                    vertxHomeDirectory);
            System.setProperty(VERTX_MODS_SYSTEM_PROPERTY, vertxHomeDirectory
                    + "/mods");
            getLog().info("Vert.X home: " + vertxHomeDirectory);
        }

        if (vertxModulesDirectory != null) {
            System.setProperty(VERTX_MODS_SYSTEM_PROPERTY,
                    vertxModulesDirectory);
            getLog().info("Vert.X modules: " + vertxModulesDirectory);
        }

        final List<String> args = new ArrayList<>();

        if (moduleName != null) {
            getLog().info("Launching module [" + moduleName + "]");
            args.add(moduleName);
        } else {
            throw new MojoExecutionException(
                    "You have to specify the moduleName parameter.");
        }

        final String classpath = getFullClasspath();
        args.add("-cp");
        args.add(classpath);

        if (configFile != null) {
            args.add("-conf");
            args.add(configFile.getAbsolutePath());
        }

        args.add("-instances");
        args.add(instances.toString());

        return args;
    }

    /**
     * @return classpath including Maven dependencies
     * @throws MojoExecutionException
     */
    protected String getFullClasspath() throws MojoExecutionException {
        final StringBuilder classpathBuilder = new StringBuilder(
                getDefaultClasspathString());
        if (classpath != null) {
            classpathBuilder.append(CP_SEPARATOR).append(classpath);
        }
        try {
            @SuppressWarnings("unchecked")
            final List<String> runtimeClasspathElements = mavenProject
                    .getRuntimeClasspathElements();
            for (final String element : runtimeClasspathElements) {
                classpathBuilder.append(CP_SEPARATOR).append(element);
            }
        } catch (final DependencyResolutionRequiredException e) {
            throw new MojoExecutionException(
                    "Could not list runtime classpath elements");
        }

        final String fullClasspath = classpathBuilder.toString();

        getLog().debug("Full classpath [" + fullClasspath + "]");

        return fullClasspath;
    }

    /**
     * Build a default classpath.
     * 
     * If the packaging of the Maven project is jar then add the path of the
     * compiled jar file to the default classpath.
     * 
     * @return default classpath
     */
    private String getDefaultClasspathString() {
        String defaultClasspath = ".";

        if (mavenProject.getPackaging().toUpperCase().equals("JAR")) {
            defaultClasspath += CP_SEPARATOR
                    + mavenProject.getBuild().getDirectory().replace("\\", "/")
                    + "/" + mavenProject.getBuild().getFinalName() + "."
                    + mavenProject.getPackaging();
        }

        getLog().debug("Default classpath [" + defaultClasspath + "]");
        return defaultClasspath;
    }

    protected JsonObject getConf(List<String> args) {
        JsonObject config = null;

        if (args.contains("-conf")) {
            final String conf = args.get(args.indexOf("-conf") + 1);

            final String confContent = readConfigFile(conf);

            if (confContent != null && !confContent.isEmpty()) {
                config = new JsonObject(confContent);
            }
        }
        return config;
    }

    private String readConfigFile(final String strFile) {
        final File file = new File(strFile);
        final URI uri = file.toURI();
        byte[] bytes = null;
        try {
            bytes = readAllBytes(java.nio.file.Paths.get(uri));
        } catch (final IOException e) {
            // just returns an empty string. Nothing to be thrown
        }

        return new String(bytes);
    }

    protected int getInstances(List<String> args) {
        int instances = 1;
        if (args.contains("-instances")) {
            final String instancesStr = args
                    .get(args.indexOf("-instances") + 1);
            instances = Integer.valueOf(instancesStr);
        }
        return instances;
    }
}