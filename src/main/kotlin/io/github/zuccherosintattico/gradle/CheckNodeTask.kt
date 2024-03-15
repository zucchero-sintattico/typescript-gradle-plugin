package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.NodeCommandsExtension.nodeVersion
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
        runCatching { shellRun { nodeVersion() } }
            .onSuccess { logger.quiet("Node is installed at version $it") }
            .onFailure { logger.error("Node is not installed") }
    }
}
