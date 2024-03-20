package io.github.zuccherosintattico.utils

import io.github.zuccherosintattico.utils.Architecture.AARCH64
import io.github.zuccherosintattico.utils.Architecture.AMD64
import io.github.zuccherosintattico.utils.Architecture.X64
import io.github.zuccherosintattico.utils.Architecture.X86
import io.github.zuccherosintattico.utils.Architecture.X86_64
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedExtensions.TAR_GZ
import io.github.zuccherosintattico.utils.NodeDistribution.SupportedExtensions.ZIP
import io.github.zuccherosintattico.utils.Platform.LINUX
import io.github.zuccherosintattico.utils.Platform.MAC
import io.github.zuccherosintattico.utils.Platform.WINDOWS

internal object NodeDistribution {

    /**
     * The supported file extensions.
     */
    object SupportedExtensions {
        const val TAR_GZ = ".tar.gz"
        const val ZIP = ".zip"
    }

    private val osName: String = when (Platform.fromProperty()) {
        WINDOWS -> "win"
        MAC -> "darwin"
        LINUX -> "linux"
    }

    private val osArch: String = when (Architecture.fromProperty()) {
        X64, AMD64, X86_64 -> "x64"
        X86 -> "x86"
        AARCH64 -> "arm64"
    }

    private val format: String = when (Platform.fromProperty()) {
        WINDOWS -> ZIP
        MAC, LINUX -> TAR_GZ
    }

    /**
     * Returns the node download endpoint for the given version and platform.
     * @return the download endpoint in the form of a URL.
     * E.g. https://nodejs.org/dist/v14.17.0/node-v14.17.0-darwin-x64.tar.gz
     */
    fun endpointFromVersion(version: String): String =
        "https://nodejs.org/dist/v$version/node-v$version-$osName-$osArch$format"
}
