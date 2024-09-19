package io.github.zuccherosintattico.gradle

import io.github.zuccherosintattico.gradle.CheckNodeTask.Companion.nodeBundleFile
import io.github.zuccherosintattico.gradle.Constants.MISSING_PACKAGE_JSON_ERROR
import io.github.zuccherosintattico.gradle.Constants.MISSING_TS_CONFIG_ERROR
import io.github.zuccherosintattico.gradle.Constants.PACKAGE_JSON
import io.github.zuccherosintattico.gradle.Constants.missingProjectRoot
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
            nodeBundleFile.set(project.nodeBundleFile())
            projectDir.set((project.projectDir.toPath() / projectExtension.basePath.get()).toFile())
            outputs.upToDateWhen { false } // Don't allow gradle to mark this task as UP-TO-DATE
        }
        project.afterEvaluate {
            if (!(project.projectDir.toPath() / projectExtension.basePath.get()).toFile().exists()) {
                throw GradleException(missingProjectRoot((project.projectDir.toPath() / projectExtension.basePath.get()).toFile().absolutePath))
            }
            if (!project.fileExist(projectExtension.fromProjectBase(PACKAGE_JSON))) {
                throw GradleException(MISSING_PACKAGE_JSON_ERROR)
            }
            if (!project.fileExist(projectExtension.fromProjectBase(typescriptExtension.tsConfig.get()))) {
                throw GradleException(MISSING_TS_CONFIG_ERROR)
            }
        }
        val npmDependenciesTask = project.registerTask<NpmDependenciesTask>("npmDependencies") {
            nodeBundleFile.set(checkNodeTask.flatMap { it.nodeBundleFile })
            dependsOn(checkNodeTask)
            projectDir.set((project.projectDir.toPath() / projectExtension.basePath.get()).toFile())
        }
        val compileTypescriptTask = project.registerTask<TypescriptTask>("compileTypescript") {
            dependsOn(npmDependenciesTask)
            tsConfig.set(typescriptExtension.tsConfig)
            buildDir.set(typescriptExtension.outputDir)
            buildCommandExecutable.set(typescriptExtension.buildCommandExecutable)
            buildCommand.set(typescriptExtension.buildCommand)
            projectDir.set((project.projectDir.toPath() / projectExtension.basePath.get()).toFile())
            nodeBundleFile.set(checkNodeTask.flatMap { it.nodeBundleFile })
        }
        project.registerTask<RunJSTask>("runJS") {
            dependsOn(compileTypescriptTask)
            entrypoint.set(typescriptExtension.entrypoint)
            buildDir.set(typescriptExtension.outputDir)
            nodeBundleFile.set(checkNodeTask.flatMap { it.nodeBundleFile })
            projectDir.set((project.projectDir.toPath() / projectExtension.basePath.get()).toFile())
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
