package io.github.zuccherosintattico.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

class TypescriptPluginTest : AnnotationSpec() {

    companion object {
        private const val TEST_DIR_PREFIX = "test"
    }

    @BeforeAll
    fun setup() {
        val testkitProperties = javaClass.classLoader.getResource("testkit-gradle.properties")?.readText()
        checkNotNull(testkitProperties) {
            "No file testkit-gradle.properties was generated"
        }
    }

    private fun toPluginEnv(env: String): String = "src/test/resources/plugin-env/$env"

    private fun getTempDirectoryWithResources(resourcesPath: String = toPluginEnv("base")): Path =
        createTempDirectory(TEST_DIR_PREFIX)
            .also { folder ->
                val testResources = File(resourcesPath).toPath()
                Files.walk(testResources)
                    .filter { it != testResources }
                    .map { testResources.relativize(it) }
                    .map { testResources.resolve(it) to folder.resolve(it) }
                    .forEach { (source, destination) ->
                        Files.createDirectories(destination.parent)
                        Files.copy(source, destination)
                    }
            }

    private fun Path.executeGradleTask(vararg arguments: String): BuildResult =
        with(GradleRunner.create()) {
            withProjectDir(toFile())
            withArguments(*arguments)
            withPluginClasspath()
            build()
        }

    private fun Path.walkRelative(): List<File> = toFile().walk().map { it.relativeTo(toFile()) }.toList()

    @Test
    fun `compileTypescript should work in base env`() {
        val testFolder = getTempDirectoryWithResources()
        testFolder.executeGradleTask("compileTypescript")

        testFolder.walkRelative() shouldContainAll listOf(
            "build/dist",
            "build/dist/index.js",
            "build/dist/person.js",
            "node_modules",
            "package-lock.json",
        ).map { File(it) }
    }

    @Test
    fun `Build task should be an alias for compileTypescript`() {
        val testFolder = getTempDirectoryWithResources()
        testFolder.executeGradleTask("build")

        testFolder.walkRelative() shouldContainAll listOf(
            "build/dist",
            "build/dist/index.js",
            "build/dist/person.js",
            "node_modules",
            "package-lock.json",
        ).map { File(it) }
    }

    @Test
    fun `Build command should be customizable`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("custom-build-command"))
        testFolder.executeGradleTask("build")

        testFolder.walkRelative() shouldContainAll listOf(
            "dist/index.html",
            "dist/vite.svg",
            "dist/assets/index-B87vROlf.css",
            "dist/assets/index-CqfCy5sM.js",
        ).map { File(it) }
    }

    @Test
    fun `Multiple plugin should not conflict`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("multiple-plugins"))
        testFolder.executeGradleTask("build")

        testFolder.walkRelative() shouldContainAll listOf(
            "build/dist",
            "build/dist/index.js",
            "build/dist/person.js",
            "node_modules",
            "package-lock.json",
        ).map { File(it) }
    }

    @Test
    fun `runJS should run compiled Javascript`() {
        val testFolder = getTempDirectoryWithResources()

        testFolder
            .executeGradleTask("runJS")
            .output shouldContain "John is 42 years old"
    }

    @Test
    fun `runJS should work also with sub projects`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("sub-project"))

        testFolder
            .executeGradleTask("runJS", "--stacktrace")
            .output shouldContain "John is 42 years old"
    }

    @Test
    fun `Project without package json should give error`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("missing-package-json"))

        shouldThrow<UnexpectedBuildFailure> {
            testFolder.executeGradleTask("compileTypescript")
        }.message shouldContain Constants.MISSING_PACKAGE_JSON_ERROR
    }

    @Test
    fun `Project without ts config should give error`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("missing-ts-config"))

        shouldThrow<UnexpectedBuildFailure> {
            testFolder.executeGradleTask("compileTypescript")
        }.message shouldContain Constants.MISSING_TS_CONFIG_ERROR
    }

    @Test
    fun `Node should be downloaded`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("download-node"))

        testFolder
            .executeGradleTask("checkNode")
            .output shouldContain "Node is installed at version v20.7.0" // version set in tested env
    }

    @Test
    fun `Node should not be download if already here`() {
        val testFolder = getTempDirectoryWithResources(toPluginEnv("download-node"))

        testFolder
            .executeGradleTask("checkNode")
            .output shouldContain "Node is installed at version v20.7.0" // version set in tested env

        testFolder
            .executeGradleTask("checkNode", "--info")
            .output shouldContain CheckNodeTask.NODE_ALREADY_INSTALLED
    }
}
