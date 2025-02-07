package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.nodeCommand
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import kotlin.io.path.div

/**
 * A task to install NPM dependencies.
 */
abstract class RunJSTask : DefaultTask() {

    init {
        group = "Node"
        description = "Run compiled JavaScript files within node"
    }

    /**
     * The entrypoint to run.
     */
    @get:Input
    abstract val entrypoint: Property<String>

    /**
     * The build directory.
     */
    @get:InputDirectory
    abstract val buildDir: Property<String>

    /**
     * The custom npm prefix path.
     */
    @get:Input
    abstract val prefixPath: Property<String>

    /**
     * The action to run the compiled JavaScript files within node.
     */
    @TaskAction
    fun run() {
        runCatching {
            shellRun(projectDir) {
                nodeCommand(project, Paths.get(buildDir.get(), entrypoint.get()).toString())
            }
        }
            .onSuccess { logger.lifecycle(it) }
            .onFailure { throw GradleException("Failed to run: $it") }
    }

    private val projectDir get() = (project.projectDir.toPath() / prefixPath.get()).toFile()
}
