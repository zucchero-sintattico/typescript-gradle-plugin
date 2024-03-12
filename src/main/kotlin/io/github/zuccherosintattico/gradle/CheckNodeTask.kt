package io.github.zuccherosintattico.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * A task to check if Node is installed.
 */
open class CheckNodeTask : DefaultTask() {
    init {
        group = "Node"
        description = "Check if Node is installed"
    }

    /**
     * The task action to check if Node is installed.
     */
    @TaskAction
    fun checkNode() {
        val res = Runtime.getRuntime().runCatching { exec("node --version").waitFor() }
        if (res.isSuccess) {
            logger.quiet("Node is installed")
        } else {
            logger.error("Node is not installed")
        }
    }
}
