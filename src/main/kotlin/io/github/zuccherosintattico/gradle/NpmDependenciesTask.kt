package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NpmCommandsExtension.npmInstall
import org.gradle.api.DefaultTask
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
        logger.quiet("Installing NPM dependencies")
        val out = shellRun(project.projectDir) { npmInstall() }
        logger.quiet(out)
    }
}
