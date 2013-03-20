package org.vertx.maven.plugin.mojo;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.vertx.java.core.Handler;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

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

/**
 * <p>
 * This goal is used to run a vert.x verticle in it's own instance.
 * </p>
 * q
 * <p>
 * The plugin forks a parallel lifecycle to ensure that the "package" phase has
 * been completed before invoking vert.x. This means that you do not need to
 * explicitly execute a "mvn package" first. It also means that a
 * "mvn clean vertx:run" will ensure that a full fresh compile and package is
 * done before invoking vert.x.
 * </p>
 * 
 * @description Runs vert.x directly from a Maven project.
 */
@Mojo(name = "runmod", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class VertxRunModMojo extends BaseVertxMojo {

    @Override
    public void execute() throws MojoExecutionException {

        args = getArgs();
        args.add(0, VERTX_RUNMOD_COMMAND);

        try {
            PlatformManager pm = PlatformLocator.factory
                    .createPlatformManager();
            final CountDownLatch latch = new CountDownLatch(1);
            pm.deployModule(args.get(1), getConf(args), getInstances(args),
                    new Handler<String>() {
                        public void handle(String deploymentID) {
                            if (deploymentID != null) {
                                System.out.println("CTRL-C to stop server");
                            } else {
                                System.out
                                        .println("Could not find the module. Did you forget to do mvn package?");
                                // System.out
                                // .println("Press CTRL-C to exit and do `mvn package`");
                                latch.countDown();
                            }
                        }
                    });
            latch.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
