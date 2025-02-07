package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npmInstall
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.div

/**
 * A task to install NPM dependencies.
 */
abstract class NpmDependenciesTask : DefaultTask() {

    init {
        group = "Node"
        description = "Install NPM dependencies"
    }

    /**
     * The custom npm prefix path.
     */
    @get:Input
    abstract val prefixPath: Property<String>

    /**
     The action to install NPM dependencies.
     */
    @TaskAction
    fun installNpmDependencies() {
        runCatching { shellRun(projectDir) { npmInstall(project) } }
            .onSuccess { logger.quiet("Installed NPM dependencies") }
            .onFailure { throw GradleException("Failed to install NPM dependencies: $it") }
    }

    private val projectDir get() = (project.projectDir.toPath() / prefixPath.get()).toFile()
}
