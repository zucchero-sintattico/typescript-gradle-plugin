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
     * The path to the TypeScript source set.
     */
    val entrypoint: Property<String> = objects.propertyWithDefault("src/main/typescript/index.ts")

    /**
     * The path to the TypeScript output directory.
     */
    val outputDir: Property<String> = objects.propertyWithDefault("build/dist")

    companion object {
        private const val serialVersionUID = 1L
    }
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
    val version: Property<String> = objects.propertyWithDefault("v21.7.1")

    companion object {
        private const val serialVersionUID = 1L
    }
}
