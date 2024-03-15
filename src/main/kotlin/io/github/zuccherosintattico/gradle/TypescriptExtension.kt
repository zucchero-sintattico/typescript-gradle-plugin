package io.github.zuccherosintattico.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.Serializable

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

        private inline fun <reified T> ObjectFactory.propertyWithDefault(value: T): Property<T> =
            property<T>().convention(value)
    }
}
