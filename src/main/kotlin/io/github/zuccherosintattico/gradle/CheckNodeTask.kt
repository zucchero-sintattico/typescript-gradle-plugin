package io.github.zuccherosintattico.gradle

import com.lordcodes.turtle.shellRun
import io.github.zuccherosintattico.utils.ArchiveExtractor
import io.github.zuccherosintattico.utils.NodeCommandsExtension.nodeVersion
import io.github.zuccherosintattico.utils.NodeDistribution
import io.github.zuccherosintattico.utils.NodePathBundle
import io.github.zuccherosintattico.utils.NodePathBundle.Companion.toNodePathBundle
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.provideDelegate
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.createTempDirectory
import kotlin.io.path.extension
import kotlin.io.path.name

/**
 * A task to check if Node is installed.
 */
abstract class CheckNodeTask : DefaultTask() {

    companion object {
        private const val TEMP_DIR_PREFIX = "temp"
        private const val NODE_DIR = "node"
        private fun Project.nodeBuildDir(): Directory = layout.buildDirectory.get().dir(NODE_DIR)

        /**
         * The file containing the paths to the node, npm, and npx executables.
         */
        const val NODE_BUNDLE_PATHS_FILE = "nodePaths.properties"

        /**
         * The project's build directory.
         */
        fun Project.nodeBundleFile(): Path = nodeBuildDir().asFile.resolve(NODE_BUNDLE_PATHS_FILE).toPath()
    }

    init {
        group = "Node"
        description = "Check if Node is installed"
    }

    /**
     * True if Node should be installed. Default is false.
     */
    @get:Input
    abstract val shouldInstall: Property<Boolean>

    /**
     * True if Node should be installed. Default is false.
     */
    @get:Input
    @get:Optional
    abstract val zipUrl: Property<String>

    /**
     * The version of Node to install. Ignored if [zipUrl] is specified.
     */
    @get:Input
    abstract val version: Property<String>

    private val nodeBuildDir: Directory by lazy {
        project.nodeBuildDir()
            .also { logger.quiet("Node will be installed in $it") }
    }

    /**
     * The task action to check if Node is installed.
     */
    @TaskAction
    fun installAndCheckNode() {
        val nodePathBundle = if (shouldInstall.get()) {
            downloadNode().also {
                addPermissionsToNode(it)
            }
        } else {
            NodePathBundle.defaultPathBundle
        }
        installNodeByBundle(nodePathBundle)
        check()
    }

    private fun installNodeByBundle(nodePathBundle: NodePathBundle) {
        if (!Files.exists(nodeBuildDir.asFile.toPath())) {
            Files.createDirectories(nodeBuildDir.asFile.toPath())
        }
        nodePathBundle.saveToPropertiesFile(project.nodeBundleFile())
    }

    private fun addPermissionsToNode(nodePathBundle: NodePathBundle) {
        System.getProperty("os.name").let {
            if (it.contains(NodeDistribution.SupportedSystem.LINUX) ||
                it.contains(NodeDistribution.SupportedSystem.MAC)
            ) {
                nodePathBundle.toSet().forEach { executable ->
                    Files.setPosixFilePermissions(
                        executable,
                        setOf(
                            PosixFilePermission.OWNER_EXECUTE,
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                        ),
                    )
                }
            }
        }
    }

    private fun check() {
        runCatching { shellRun(project.projectDir) { nodeVersion(project) } }
            .onSuccess { logger.quiet("Node is installed at version $it") }
            .onFailure { logger.error("Node not found: $it") }
    }

    private fun downloadNode(): NodePathBundle {
        val urlToDownload = zipUrl.orElse(NodeDistribution.endpointFromVersion(version.get())).get()
            .also { logger.quiet("Download node from: $it") }
            .let { URL(it) }
        /*
        This call has the side effect to
            -  download the Node distribution;
            -  extract it;
            -  move it to the build directory.
        The structure will be e.g. build/node/node-v20.11.1-darwin-x64/...
         */
        return downloadFromUrlAndMoveToBuildDirectory(urlToDownload).toNodePathBundle()
    }

    private fun downloadFromUrlAndMoveToBuildDirectory(url: URL): Path {
        return downloadFromUrl(url).let {
            when {
                it.name.endsWith(NodeDistribution.SupportedExtensions.TAR_GZ) ->
                    ArchiveExtractor.extractTarGz(it.toFile(), nodeBuildDir.asFile)
                it.name.endsWith(NodeDistribution.SupportedExtensions.ZIP) ->
                    ArchiveExtractor.extractZip(it.toFile(), nodeBuildDir.asFile)
                else -> throw GradleException("Unsupported archive format: ${it.extension}")
            }
        }
    }

    /**
     * Retrieve the name of the file, including the extension.
     */
    private fun URL.name() = file.split("/").last()

    private fun downloadFromUrl(url: URL): Path {
        val tempDir = createTempDirectory(TEMP_DIR_PREFIX).also { logger.quiet("Node will be downloaded in $it") }
        val nodeArchive = tempDir.resolve(url.name())
        runCatching {
            url.openStream().use {
                Files.copy(it, nodeArchive)
            }
        }.onFailure { throw GradleException("Error while downloading Node: $it") }
        return nodeArchive
    }
}
