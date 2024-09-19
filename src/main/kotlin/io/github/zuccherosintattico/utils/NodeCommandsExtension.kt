package io.github.zuccherosintattico.utils

import com.lordcodes.turtle.ShellScript
import org.gradle.api.file.RegularFileProperty
import java.nio.file.Path

/**
 * The extension for Node commands for [ShellScript].
 */
object NodeCommandsExtension {

    /**
     * Get the version of installed Node.
     */
    fun ShellScript.nodeVersion(nodeBundleFile: RegularFileProperty): String = nodeCommand(nodeBundleFile, "--version")

    /**
     * Install the dependencies.
     */
    fun ShellScript.npmInstall(nodeBundleFile: RegularFileProperty): String = npmCommand(nodeBundleFile, "install")

    private fun RegularFileProperty.loadNodeBundlePaths(): NodePathBundle = NodePathBundle.loadFromPropertiesFile(this)

    private fun ShellScript.runCommand(
        nodeBundleFile: RegularFileProperty,
        withPath: NodePathBundle.() -> Path,
        vararg arguments: String,
    ): String =
        command(nodeBundleFile.loadNodeBundlePaths().withPath().toString(), arguments.toList())

    /**
     * Run the Node command.
     */
    fun ShellScript.nodeCommand(nodeBundleFile: RegularFileProperty, vararg arguments: String): String =
        runCommand(nodeBundleFile, NodePathBundle::node, *arguments)

    /**
     * Run the NPM command.
     */
    fun ShellScript.npmCommand(nodeBundleFile: RegularFileProperty, vararg arguments: String): String =
        runCommand(nodeBundleFile, NodePathBundle::npm, *arguments)

    /**
     * Run the NPX command.
     */
    fun ShellScript.npxCommand(nodeBundleFile: RegularFileProperty, vararg arguments: String): String =
        runCommand(nodeBundleFile, NodePathBundle::npx, *arguments)
}
