package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NpmCommandsExtension.npxCommand
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
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
    @get:OutputFile
    abstract val buildDir: Property<String>

    /**
     * The task action.
     */
    @TaskAction
    fun compileTypescript() {
        logger.quiet("Compiling TypeScript files in ${entrypoint.get()}")
        shellRun(project.projectDir) {
            npxCommand("tsc", "--outDir", buildDir.get(), entrypoint.get())
        }.also { logger.quiet("Compiled: $it") }
    }
}
