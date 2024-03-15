package io.github.zuccherosintattico.utils

import com.lordcodes.turtle.ShellScript

/**
 * The extension for NPM commands for [ShellScript].
 */
object NpmCommandsExtension {

    private fun String.cmdIfOnWindows(): String = System.getProperty("os.name").let { os ->
        when {
            os.contains("Windows") -> "$this.cmd"
            else -> this
        }
    }

    private val npmCommand: String = "npm".cmdIfOnWindows()
    private val npxCommand: String = "npx".cmdIfOnWindows()

    /**
     * Install the dependencies.
     */
    fun ShellScript.npmInstall(): String = npmCommand("install")

    /**
     * Run the NPM command.
     */
    fun ShellScript.npmCommand(vararg arguments: String): String = command(npmCommand, arguments.toList())

    /**
     * Run the NPX command.
     */
    fun ShellScript.npxCommand(vararg arguments: String): String = command(npxCommand, arguments.toList())
}
