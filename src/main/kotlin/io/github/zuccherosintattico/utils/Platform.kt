package io.github.zuccherosintattico.utils

internal enum class Platform {
    WINDOWS,
    MAC,
    LINUX,
    ;

    companion object {
        fun fromProperty(): Platform {
            val os = System.getProperty("os.name")
            return when {
                os.contains("Mac") -> MAC
                os.contains("Windows") -> WINDOWS
                os.contains("Linux") -> LINUX
                else -> throw PlatformError("Unsupported OS: ${System.getProperty("os.name")}")
            }
        }
    }
}

internal enum class Architecture {
    X64,
    AMD64,
    X86_64,
    X86,
    AARCH64,
    ;

    companion object {
        fun fromProperty(): Architecture {
            val arch = System.getProperty("os.arch")
            return when {
                arch.contains("x64") -> X64
                arch.contains("amd64") -> AMD64
                arch.contains("x86_64") -> X86_64
                arch.contains("x32") -> X86
                arch.equals("aarch64") -> AARCH64
                else -> throw ArchitectureError("Unsupported architecture: $arch")
            }
        }
    }
}

internal data class PlatformError(
    override val message: String,
) : Error(message)

internal data class ArchitectureError(
    override val message: String,
) : Error(message)
