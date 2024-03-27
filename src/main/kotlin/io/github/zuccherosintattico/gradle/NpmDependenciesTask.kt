package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npmInstall
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * A task to install NPM dependencies.
 */
open class NpmDependenciesTask : DefaultTask() {

    init {
        group = "Node"
        description = "Install NPM dependencies"
    }

    /**
     The action to install NPM dependencies.
     */
    @TaskAction
    fun installNpmDependencies() {
        runCatching { shellRun(project.projectDir) { npmInstall(project) } }
            .onSuccess { logger.quiet("Installed NPM dependencies") }
            .onFailure { throw GradleException("Failed to install NPM dependencies: $it") }
    }
}
