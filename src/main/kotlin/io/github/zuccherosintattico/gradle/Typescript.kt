package io.github.zuccherosintattico.gradle

import io.github.zuccherosintattico.gradle.Constants.MISSING_PACKAGE_JSON_ERROR
import io.github.zuccherosintattico.gradle.Constants.MISSING_TS_CONFIG_ERROR
import io.github.zuccherosintattico.gradle.Constants.PACKAGE_JSON
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div

/**
 * A plugin to compile TypeScript files.
 */
open class Typescript : Plugin<Project> {
    override fun apply(project: Project) {
        val projectExtension = project.extensions.create<ProjectExtension>("project")
        val nodeExtension = project.extensions.create<NodeExtension>("node")
        val typescriptExtension = project.extensions.create<TypescriptExtension>("typescript")
        val checkNodeTask = project.registerTask<CheckNodeTask>("checkNode") {
            shouldInstall.set(nodeExtension.shouldInstall)
            zipUrl.set(nodeExtension.zipUrl)
            version.set(nodeExtension.version)
        }
        project.afterEvaluate {
            if (!project.fileExist(projectExtension.fromProjectBase(PACKAGE_JSON))) {
                throw GradleException(MISSING_PACKAGE_JSON_ERROR)
            }
            if (!project.fileExist(projectExtension.fromProjectBase(typescriptExtension.tsConfig.get()))) {
                throw GradleException(MISSING_TS_CONFIG_ERROR)
            }
        }
        val npmDependenciesTask = project.registerTask<NpmDependenciesTask>("npmDependencies") {
            dependsOn(checkNodeTask)
            prefixPath.set(projectExtension.basePath)
        }
        val compileTypescriptTask = project.registerTask<TypescriptTask>("compileTypescript") {
            dependsOn(npmDependenciesTask)
            tsConfig.set(typescriptExtension.tsConfig)
            buildDir.set(typescriptExtension.outputDir)
            buildCommandExecutable.set(typescriptExtension.buildCommandExecutable)
            buildCommand.set(typescriptExtension.buildCommand)
            prefixPath.set(projectExtension.basePath)
        }
        project.registerTask<RunJSTask>("runJS") {
            dependsOn(compileTypescriptTask)
            entrypoint.set(typescriptExtension.entrypoint)
            buildDir.set(typescriptExtension.outputDir)
            prefixPath.set(projectExtension.basePath)
        }
        project.apply<org.gradle.api.plugins.BasePlugin>()
        project.tasks.named("build").configure {
            it.dependsOn(compileTypescriptTask)
        }
    }

    companion object {
        private inline fun <reified T : Task> Project.registerTask(name: String, noinline action: T.() -> Unit = {}) =
            tasks.register<T>(name, action)

        private fun Project.fileExist(file: File): Boolean = file(file).exists()

        private fun ProjectExtension.fromProjectBase(file: String): File = (Path(basePath.get()) / file).toFile()
    }
}
