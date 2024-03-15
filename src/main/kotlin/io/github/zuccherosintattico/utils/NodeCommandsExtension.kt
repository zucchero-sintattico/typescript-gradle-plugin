package io.github.zuccherosintattico.utils

import com.lordcodes.turtle.ShellScript

/**
 * The extension for Node commands for [ShellScript].
 */
object NodeCommandsExtension {

    /**
     * Get the version of installed Node.
     */
    fun ShellScript.nodeVersion(): String = nodeCommand(listOf("--version"))

    private fun ShellScript.nodeCommand(arguments: List<String>): String = command("node", arguments)
}
