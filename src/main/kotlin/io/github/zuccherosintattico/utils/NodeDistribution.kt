package io.github.zuccherosintattico.utils

import io.github.zuccherosintattico.utils.NodeDistribution.SupportedExtensions.TAR_GZ
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedExtensions.ZIP
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedSystem.LINUX
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedSystem.MAC
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedSystem.WINDOWS

internal object NodeDistribution {

    /**
     * The supported file extensions.
     */
    object SupportedExtensions {
        const val TAR_GZ = ".tar.gz"
        const val ZIP = ".zip"
    }

    /**
     * The supported operating systems.
     */
    object SupportedSystem {
        const val WINDOWS = "Windows"
        const val MAC = "Mac"
        const val LINUX = "Linux"
    }

    private val osName: String = System.getProperty("os.name").let {
        when {
            it.contains("Windows") -> "win"
            it.contains("Mac") -> "darwin"
            it.contains("Linux") -> "linux"
            else -> throw PlatformError("Unsupported OS: $it")
        }
    }

    private val osArch: String = System.getProperty("os.arch").let {
        when {
            it.equals("aarch64") -> "arm64"
            it.contains("x64") -> "x64"
            it.contains("32") -> "x86"
            else -> throw PlatformError("Unsupported architecture: $it")
        }
    }

    private val format: String = System.getProperty("os.name").let {
        when {
            it.contains(WINDOWS) -> ZIP
            it.contains(MAC) -> TAR_GZ
            it.contains(LINUX) -> TAR_GZ
            else -> throw PlatformError("Unsupported OS: $it")
        }
    }

    /**
     * Returns the node download endpoint for the given version and platform.
     * @return the download endpoint in the form of a URL.
     * E.g. https://nodejs.org/dist/v14.17.0/node-v14.17.0-darwin-x64.tar.gz
     */
    fun endpointFromVersion(version: String): String =
        "https://nodejs.org/dist/v$version/node-v$version-$osName-$osArch$format"
}
