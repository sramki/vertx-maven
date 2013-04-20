package org.vertx.maven.plugin.mojo;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;
import static org.vertx.java.platform.PlatformLocator.factory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.vertx.java.platform.PlatformManager;

@Mojo(name = "pullindeps", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class VertxPullInDepsMojo extends BaseVertxMojo {

	@Override
	public void execute() throws MojoExecutionException {
		args = getArgs();
		args.add(0, VERTX_RUNMOD_COMMAND);

		try {
			final PlatformManager pm = factory.createPlatformManager();
			pm.pullInDependencies(moduleName, null);
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
}
