package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.gradle.BuildCommandExecutable.DEFAULT
import io.github.zuccherosintattico.gradle.BuildCommandExecutable.NODE
import io.github.zuccherosintattico.gradle.BuildCommandExecutable.NPM
import io.github.zuccherosintattico.gradle.BuildCommandExecutable.NPX
import io.github.zuccherosintattico.utils.NodeCommandsExtension.nodeCommand
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npmCommand
import io.github.zuccherosintattico.utils.NodeCommandsExtension.npxCommand
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
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
    @get:Input
    abstract val tsConfig: Property<String>

    /**
     * The buildCommandExecutable.
     */
    @get:Input
    abstract val buildCommandExecutable: Property<BuildCommandExecutable>

    /**
     * The custom build command.
     */
    @get:Input
    abstract val buildCommand: Property<String>

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
     * The task action.
     */
    @TaskAction
    fun compileTypescript() {
        runCatching {
            shellRun(projectDir.asFile.get()) {
                when (buildCommandExecutable.get()) {
                    DEFAULT ->
                        npxCommand(
                            nodeBundleFile,
                            "tsc",
                            "--project",
                            tsConfig.get(),
                            "--outDir",
                            buildDir.get(),
                        )
                    NODE -> nodeCommand(nodeBundleFile, *buildCommand.get().split(" ").toTypedArray())
                    NPM -> npmCommand(nodeBundleFile, *buildCommand.get().split(" ").toTypedArray())
                    NPX -> npxCommand(nodeBundleFile, *buildCommand.get().split(" ").toTypedArray())
                    else -> throw GradleException("Unknown build command executable: ${buildCommandExecutable.get()}")
                }
            }
        }.onSuccess { logger.quiet("Compilation successful") }
            .onFailure { throw GradleException("Failed to compile TypeScript files: $it") }
    }
}
