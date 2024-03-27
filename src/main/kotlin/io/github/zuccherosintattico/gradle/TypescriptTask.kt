package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npxCommand
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Typescript task.
 */
abstract class TypescriptTask : DefaultTask() {

    init {
        group = "Node"
        description = "Compile TypeScript files"
    }

    /**
     * The build directory.
     */
    @get:OutputDirectory
    abstract val buildDir: Property<String>

    /**
     * The path to the TypeScript configuration file.
     */
    @get:InputFile
    abstract val tsConfig: Property<String>

    /**
     * The task action.
     */
    @TaskAction
    fun compileTypescript() {
        runCatching {
            shellRun(project.projectDir) {
                npxCommand(
                    project,
                    "tsc",
                    "--project",
                    tsConfig.get(),
                    "--outDir",
                    buildDir.get(),
                )
            }
        }
            .onSuccess { logger.quiet("Compilation successful") }
            .onFailure { throw GradleException("Failed to compile TypeScript files: $it") }
    }
}
