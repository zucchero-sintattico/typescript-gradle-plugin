package io.github.zuccherosintattico.gradle

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.paths.shouldExist
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files
import kotlin.io.path.createTempDirectory

class TypescriptPluginTest : AnnotationSpec() {

    @BeforeAll
    fun setup() {
        val testkitProperties = javaClass.classLoader.getResource("testkit-gradle.properties")?.readText()
        checkNotNull(testkitProperties) {
            "No file testkit-gradle.properties was generated"
        }
    }

    @Test
    fun `test the plugin`() {
        val folder = createTempDirectory("test")
        folder.shouldExist()

        val testResources = File("src/test/resources/plugin-base-env").toPath()
        Files.walk(testResources)
            .filter { it != testResources }
            .map { testResources.relativize(it) }
            .map { testResources.resolve(it) to folder.resolve(it) }
            .forEach { (source, destination) ->
                Files.createDirectories(destination.parent)
                Files.copy(source, destination)
            }

        println(folder.toFile())

        val result = with(GradleRunner.create()) {
            withProjectDir(folder.toFile())
            withArguments("compileTypescript", "--stacktrace")
            withPluginClasspath()
            build()
        }

        result.output.lines().forEach { println(it) }

//        ProcessBuilder("npx", "tsc", "--outDir", "./build/bin", "src/main/typescript/*.ts")
//            .directory(folder.toFile())
//            .inheritIO()
//            .start()
//            .waitFor()
    }
}
