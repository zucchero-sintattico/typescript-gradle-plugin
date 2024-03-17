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

    companion object {
        /**
         * The error message when package.json is not found.
         */
        const val PACKAGE_JSON_ERROR = "package.json not found"
    }

    init {
        group = "Node"
        description = "Install NPM dependencies"
    }

    /**
     The action to install NPM dependencies.
     */
    @TaskAction
    fun installNpmDependencies() {
        if (!project.file("package.json").exists()) {
            throw GradleException(PACKAGE_JSON_ERROR)
        }
        runCatching { shellRun(project.projectDir) { npmInstall(project) } }
            .onSuccess { logger.quiet("Installed NPM dependencies") }
            .onFailure { throw GradleException("Failed to install NPM dependencies: $it") }
    }
}
