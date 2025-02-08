package io.github.zuccherosintattico.utils

import com.lordcodes.turtle.ShellScript
import io.github.zuccherosintattico.gradle.CheckNodeTask.Companion.nodeBundleFile
import org.gradle.api.Project
import java.nio.file.Path

/**
 * The extension for Node commands for [ShellScript].
 */
object NodeCommandsExtension {
    /**
     * Get the version of installed Node.
     */
    fun ShellScript.nodeVersion(project: Project): String = nodeCommand(project, "--version")

    /**
     * Install the dependencies.
     */
    fun ShellScript.npmInstall(project: Project): String = npmCommand(project, "install")

    private fun Project.loadNodeBundlePaths(): NodePathBundle = NodePathBundle.loadFromPropertiesFile(nodeBundleFile())

    private fun ShellScript.runCommand(
        project: Project,
        withPath: NodePathBundle.() -> Path,
        vararg arguments: String,
    ): String = command(project.loadNodeBundlePaths().withPath().toString(), arguments.toList())

    /**
     * Run the Node command.
     */
    fun ShellScript.nodeCommand(
        project: Project,
        vararg arguments: String,
    ): String = runCommand(project, NodePathBundle::node, *arguments)

    /**
     * Run the NPM command.
     */
    fun ShellScript.npmCommand(
        project: Project,
        vararg arguments: String,
    ): String = runCommand(project, NodePathBundle::npm, *arguments)

    /**
     * Run the NPX command.
     */
    fun ShellScript.npxCommand(
        project: Project,
        vararg arguments: String,
    ): String = runCommand(project, NodePathBundle::npx, *arguments)
}
