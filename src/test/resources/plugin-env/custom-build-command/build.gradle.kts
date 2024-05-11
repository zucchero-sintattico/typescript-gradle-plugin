import io.github.zuccherosintattico.gradle.BuildCommandExecutable

plugins {
    id("io.github.zucchero-sintattico.typescript-gradle-plugin")
}

typescript {
    buildCommandExecutable = BuildCommandExecutable.NPM
    buildCommand = "run build"
}
