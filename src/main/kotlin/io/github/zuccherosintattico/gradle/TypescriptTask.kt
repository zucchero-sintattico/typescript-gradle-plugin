package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npxCommand
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Typescript task.
 */
abstract class TypescriptTask : DefaultTask() {

    /**
     * The source set to compile.
     */
    @get:Input
    abstract val entrypoint: Property<String>

    /**
     * The build directory.
     */
    @get:OutputDirectory
    abstract val buildDir: Property<String>

    /**
     * The task action.
     */
    @TaskAction
    fun compileTypescript() {
        runCatching {
            shellRun(project.projectDir) { npxCommand(project, "tsc", "--outDir", buildDir.get(), entrypoint.get()) }
        }
            .onSuccess { logger.quiet("Compiled: $it") }
            .onFailure { throw GradleException("Failed to compile TypeScript files: $it") }
    }
}
