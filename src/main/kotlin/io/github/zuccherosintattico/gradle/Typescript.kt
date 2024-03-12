package io.github.zuccherosintattico.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import java.io.Serializable

/**
 * Just a template.
 */
open class Typescript : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<TypescriptExtension>("typescript")

        val checkNodeTask = project.tasks.register<CheckNodeTask>("checkNode")

        project.tasks.register<TypescriptTask>("compileTypescript") {
            dependsOn(checkNodeTask)
            sourceSet.set(extension.sourceSet)
        }
    }
}

/**
 * Just a template.
 */
open class TypescriptTask : DefaultTask() {

    /**
     * Just a template.
     */
    @Input
    val sourceSet: Property<String> = project.objects.property()

    /**
     * Read-only property calculated from the greeting.
     */
    @Internal
    val message: Provider<String> = sourceSet.map { "Hello from $it" }

    /**
     * Just a template.
     */
    @TaskAction
    fun printMessage() {
        logger.quiet(message.get())
    }
}

/**
 * Just a template.
 */
open class TypescriptExtension(objects: ObjectFactory) : Serializable {

    /**
     * Just a template.
     */
    val sourceSet: Property<String> = objects.property<String>().convention("src/main/typescript")

    companion object {
        private const val serialVersionUID = 1L
    }
}
