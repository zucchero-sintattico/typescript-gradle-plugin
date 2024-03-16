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

    private fun getTempDirectoryWithResources(resourcesPath: String = "src/test/resources/plugin-base-env"): Path =
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
    fun `test base configuration`() {
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
    fun `test missing package json`() {
        val testFolder = getTempDirectoryWithResources("src/test/resources/missing-package-json-env")

        shouldThrow<UnexpectedBuildFailure> {
            testFolder.executeGradleTask("compileTypescript")
        }.message shouldContain NpmDependenciesTask.PACKAGE_JSON_ERROR
    }
}