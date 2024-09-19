package io.github.zuccherosintattico.gradle

import io.github.zuccherosintattico.gradle.Utils.propertyWithDefault
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.Serializable

private object Utils {
    inline fun <reified T> ObjectFactory.propertyWithDefault(value: T): Property<T> =
        property<T>().convention(value)
}

/**
 * The extension for configuring TypeScript plugin.
 */
open class TypescriptExtension(objects: ObjectFactory) : Serializable {

    /**
     * The name (or path) of the entrypoint file to run within Node.
     * This would be appended to [outputDir] to create the full path.
     * Default is "app.js".
     */
    val entrypoint: Property<String> = objects.propertyWithDefault("app.js")

    /**
     * The path to the TypeScript output directory.
     * Default is "build/dist".
     */
    val outputDir: Property<String> = objects.propertyWithDefault("build/dist")

    /**
     * The path to the TypeScript configuration file.
     * Default is "tsconfig.json".
     */
    val tsConfig: Property<String> = objects.propertyWithDefault("tsconfig.json")

    /**
     * Custom build command type.
     * Default is [BuildCommandExecutable.DEFAULT]
     * and the plugin will run the following command:
     * ```
     * npx tsc --project [tsConfig] --outDir [outputDir]
     * ```
     * If [buildCommandExecutable] is set to different value, the plugin will execute the [buildCommand] instead.
     */
    val buildCommandExecutable: Property<BuildCommandExecutable> =
        objects.propertyWithDefault(BuildCommandExecutable.DEFAULT)

    /**
     * Custom build command.
     * @see [buildCommandExecutable]
     */
    val buildCommand: Property<String> = objects.propertyWithDefault("")

    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * The build command executable.
 */
enum class BuildCommandExecutable {
    DEFAULT,
    NODE,
    NPM,
    NPX,
}

/**
 * The extension for configuring TypeScript plugin.
 */
open class NodeExtension(objects: ObjectFactory) : Serializable {

    /**
     * The path to the TypeScript source set.
     */
    val shouldInstall: Property<Boolean> = objects.propertyWithDefault(false)

    /**
     * The path to the TypeScript output directory. If specified, the plugin will download Node from this URL.
     */
    val zipUrl: Property<String> = objects.property()

    /**
     * The version of Node to install. Ignored if [zipUrl] is specified.
     */
    val version: Property<String> = objects.propertyWithDefault("21.7.1")

    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * The extension for configuring the project.
 */
open class ProjectExtension(objects: ObjectFactory) : Serializable {

    /**
     * The prefix to add to the project directory.
     */
    val basePath: Property<String> = objects.propertyWithDefault("")

    companion object {
        private const val serialVersionUID = 1L
    }
}
