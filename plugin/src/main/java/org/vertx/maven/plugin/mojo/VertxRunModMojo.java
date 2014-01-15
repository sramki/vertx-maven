package org.vertx.maven.plugin.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.platform.PlatformManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;
import static org.vertx.java.platform.PlatformLocator.factory;

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
 * @description Runs vert.x directly from a Maven project.
 */
@Mojo(name = "runMod", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class VertxRunModMojo extends BaseVertxMojo {

  @Override
  public void execute() throws MojoExecutionException {

    try {
      String vertxMods = System.getenv("VERTX_MODS");
      if (vertxMods != null) {
        modsDir = new File(vertxMods);
      }
      System.setProperty("vertx.mods", modsDir.getCanonicalPath());

      // We need to add some extra entries to the classpath so that any overrridden Vert.x platform config,
      // e.g. cluster.xml, langs.properties etc can picked up when running the module
      // Users can put such config either in a src/main/platform_lib directory (if they don't want it in the module)
      // or in a src/main/resources/platform_lib directory (if they want it in the module, e.g. for fatjars)
      List<URL> urls = new ArrayList<>();
      addURLs(urls, "src/main/platform_lib");
      addURLs(urls, "src/main/resources/platform_lib");
      final URL[] extraClasspath = urls.toArray(new URL[urls.size()]);

      final PlatformManager pm = factory.createPlatformManager();

      final CountDownLatch latch = new CountDownLatch(1);
      pm.createModuleLink(moduleName, new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> asyncResult) {
          runMod(pm, extraClasspath, latch);
        }
      });
      latch.await(MAX_VALUE, MILLISECONDS);
    } catch (final Exception e) {
      throw new MojoExecutionException(e.getMessage());
    }
  }

  protected void runMod(PlatformManager pm, URL[] classpath, final CountDownLatch latch) {
    ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();
    try {
      System.setProperty("vertx.mods", modsDir.getAbsolutePath());

      // We have to create another classloader which can load resources from src/main/resources so users
      // can override the default repos.txt, langs.properties etc when running the module

      // Seriously fuck Maven for forcing us to do this and now allowing users to configure directories
      // to add to the classpath in pom.xml

      URLClassLoader urlc = new URLClassLoader(classpath, getClass().getClassLoader());

      Thread.currentThread().setContextClassLoader(urlc);

      pm.deployModule(moduleName, getConf(), instances,
        new Handler<AsyncResult<String>>() {
          @Override
          public void handle(final AsyncResult<String> event) {
            if (event.succeeded()) {
              getLog().info("CTRL-C to stop server");
            } else {
              if (!event.succeeded()) {
                getLog().error(event.cause());
              }
              latch.countDown();
            }
          }
        });
    } finally {
      Thread.currentThread().setContextClassLoader(oldTCCL);
    }
  }

  private void addURLs(List<URL> urls, String dirName) throws IOException {
    File dir = new File(dirName);
    if (dir.exists()) {
      urls.add(dir.getCanonicalFile().toURI().toURL());
      File[] files = dir.listFiles();
      if (files != null) {
        for (File file: files) {
          String path = file.getCanonicalPath();
          if (path.endsWith(".jar") || path.endsWith(".zip")) {
            urls.add(file.getCanonicalFile().toURI().toURL());
          }
        }
      }
    }
  }

}
