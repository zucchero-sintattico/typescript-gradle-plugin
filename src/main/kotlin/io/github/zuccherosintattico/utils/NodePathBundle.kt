package io.github.zuccherosintattico.utils

import io.github.zuccherosintattico.utils.NodeDistribution.SupportedSystem.MAC
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedSystem.WINDOWS
import java.nio.file.Path
import kotlin.io.path.writeText

/**
 * A bundle of paths to the node, npm, and npx executables.
 */
internal data class NodePathBundle(
    val node: Path,
    val npm: Path,
    val npx: Path,
) {
    companion object {

        /**
         * Append the node, npm, and npx paths to the given path.
         */
        fun Path.toNodePathBundle(): NodePathBundle = System.getProperty("os.name").let {
            when {
                it.contains(WINDOWS) -> NodePathBundle(
                    resolve("node.exe"),
                    resolve("npm.cmd"),
                    resolve("npx.cmd"),
                )
                it.contains(MAC) or it.contains(NodeDistribution.SupportedSystem.LINUX) -> NodePathBundle(
                    resolve("bin/node"),
                    resolve("bin/npm"),
                    resolve("bin/npx"),
                )
                else -> throw PlatformError("Unsupported OS: $it")
            }
        }

        /**
         * The default node, npm, and npx paths for platforms.
         */
        val defaultPathBundle: NodePathBundle = System.getProperty("os.name").let {
            when {
                it.contains(WINDOWS) -> NodePathBundle(
                    Path.of("node.exe"),
                    Path.of("npm.cmd"),
                    Path.of("npx.cmd"),
                )
                it.contains(MAC) or it.contains(NodeDistribution.SupportedSystem.LINUX) -> NodePathBundle(
                    Path.of("node"),
                    Path.of("npm"),
                    Path.of("npx"),
                )
                else -> throw PlatformError("Unsupported OS: $it")
            }
        }

        /**
         * Load the node, npm, and npx paths from the given properties file.
         */
        fun loadFromPropertiesFile(propertiesFile: Path): NodePathBundle {
            val properties = propertiesFile.toFile().inputStream().use { input ->
                java.util.Properties().apply { load(input) }
            }
            return NodePathBundle(
                Path.of(properties.getProperty("node")),
                Path.of(properties.getProperty("npm")),
                Path.of(properties.getProperty("npx")),
            )
        }
    }

    /**
     * Save the [NodePathBundle] paths to the given properties file.
     */
    fun saveToPropertiesFile(propertiesFile: Path) {
        val nodePaths = mapOf(
            "node" to node.toString(),
            "npm" to npm.toString(),
            "npx" to npx.toString(),
        )
        propertiesFile.writeText(nodePaths.entries.joinToString("\n") { (k, v) -> "$k=$v" })
    }

    /**
     * Convert the [NodePathBundle] to a set.
     */
    fun toSet() = setOf(node, npm, npx)
}

internal data class PlatformError(override val message: String) : Error(message)
