package io.github.zuccherosintattico.gradle

/**
 * Some constants used in the plugin.
 */
object Constants {

    /**
     * The name of the package JSON file.
     */
    const val PACKAGE_JSON = "package.json"

    /**
     * The error message when package.json is not found.
     */
    const val MISSING_PACKAGE_JSON_ERROR = "package.json not found"

    /**
     * The error message when tsconfig.json is not found.
     */
    const val MISSING_TS_CONFIG_ERROR = "tsconfig.json not found"

    /**
     * The error message when the node executable is not found.
     */
    fun missingProjectRoot(root: String) = "The specified project root is missing: $root"
}
