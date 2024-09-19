package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npmInstall
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * A task to install NPM dependencies.
 */
abstract class NpmDependenciesTask : DefaultTask() {

    init {
        group = "Node"
        description = "Install NPM dependencies"
    }

    /**
     * [io.github.zuccherosintattico.utils.NodePathBundle] file location from [CheckNodeTask].
     */
    @get:InputFile
    abstract val nodeBundleFile: RegularFileProperty

    /**
     * Working directory for shell script invocations.
     */
    @get:Internal
    abstract val projectDir: RegularFileProperty

    /**
     * The action to install NPM dependencies.
     */
    @TaskAction
    fun installNpmDependencies() {
        runCatching { shellRun(projectDir.asFile.get()) { npmInstall(nodeBundleFile) } }
            .onSuccess { logger.quiet("Installed NPM dependencies") }
            .onFailure { throw GradleException("Failed to install NPM dependencies: $it") }
    }
}
