package io.github.zuccherosintattico.utils

import io.github.zuccherosintattico.utils.Platform.LINUX
import io.github.zuccherosintattico.utils.Platform.MAC
import io.github.zuccherosintattico.utils.Platform.WINDOWS
import org.gradle.api.file.RegularFileProperty
import java.nio.file.Path

/**
 * A bundle of paths to the node, npm, and npx executables.
 */
internal data class NodePathBundle(
    val node: Path,
    val npm: Path,
    val npx: Path,
) {
    companion object {
        private const val NODE = "node"
        private const val NPM = "npm"
        private const val NPX = "npx"

        /**
         * Append the node, npm, and npx paths to the given path.
         * @receiver The path should be the root of the node distribution.
         */
        fun Path.toNodePathBundle(): NodePathBundle =
            when (Platform.fromProperty()) {
                WINDOWS -> append(executableBundle)
                MAC, LINUX -> resolve("bin").append(executableBundle)
            }

        /**
         * The default node, npm, and npx executable for platforms.
         */
        val executableBundle: NodePathBundle =
            when (Platform.fromProperty()) {
                WINDOWS ->
                    NodePathBundle(
                        Path.of("$NODE.exe"),
                        Path.of("$NPM.cmd"),
                        Path.of("$NPX.cmd"),
                    )
                MAC, LINUX ->
                    NodePathBundle(
                        Path.of(NODE),
                        Path.of(NPM),
                        Path.of(NPX),
                    )
            }

        private fun Path.append(bundle: NodePathBundle): NodePathBundle =
            NodePathBundle(
                resolve(bundle.node),
                resolve(bundle.npm),
                resolve(bundle.npx),
            )

        private fun Path.adjustPathForWindows(): String =
            when (Platform.fromProperty()) {
                WINDOWS -> toString().replace("\\", "\\\\")
                else -> toString()
            }

        /**
         * Load the node, npm, and npx paths from the given properties file.
         */
        fun loadFromPropertiesFile(propertiesFile: RegularFileProperty): NodePathBundle {
            val properties =
                propertiesFile.asFile.get().inputStream().use { input ->
                    java.util.Properties().apply { load(input) }
                }
            return NodePathBundle(
                Path.of(properties.getProperty(NODE)),
                Path.of(properties.getProperty(NPM)),
                Path.of(properties.getProperty(NPX)),
            )
        }
    }

    /**
     * Save the [NodePathBundle] paths to the given properties file.
     */
    fun saveToPropertiesFile(propertiesFile: RegularFileProperty) {
        val nodePaths =
            mapOf(
                NODE to node.adjustPathForWindows(),
                NPM to npm.adjustPathForWindows(),
                NPX to npx.adjustPathForWindows(),
            )
        propertiesFile.asFile
            .get()
            .writeText(nodePaths.entries.joinToString("\n") { (k, v) -> "$k=$v" })
    }

    /**
     * Convert the [NodePathBundle] to a set.
     */
    fun toSet() = setOf(node, npm, npx)
}
