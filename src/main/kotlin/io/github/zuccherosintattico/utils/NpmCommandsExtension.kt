package io.github.zuccherosintattico.utils

import com.lordcodes.turtle.ShellScript

/**
 * The extension for NPM commands for [ShellScript].
 */
object NpmCommandsExtension {

    /**
     * Install the dependencies.
     */
    fun ShellScript.npmInstall(): String = npmCommand("install")

    /**
     * Run the NPM command.
     */
    fun ShellScript.npmCommand(vararg arguments: String): String = command("npm", arguments.toList())

    /**
     * Run the NPX command.
     */
    fun ShellScript.npxCommand(vararg arguments: String): String = command("npx", arguments.toList())
}
