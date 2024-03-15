package io.github.zuccherosintattico.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * A plugin to compile TypeScript files.
 */
open class Typescript : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<TypescriptExtension>("typescript")
        val checkNodeTask = project.registerTask<CheckNodeTask>("checkNode")
        val npmDependenciesTask = project.registerTask<NpmDependenciesTask>("npmDependencies") {
            dependsOn(checkNodeTask)
        }
        project.registerTask<TypescriptTask>("compileTypescript") {
            dependsOn(npmDependenciesTask)
            entrypoint.set(extension.entrypoint)
            buildDir.set(extension.outputDir)
        }
    }

    companion object {
        private inline fun <reified T : Task> Project.registerTask(name: String, noinline action: T.() -> Unit = {}) =
            tasks.register<T>(name, action)
    }
}
