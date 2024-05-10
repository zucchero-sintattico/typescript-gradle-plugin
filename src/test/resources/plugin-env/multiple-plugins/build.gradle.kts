plugins {
    id("io.github.zucchero-sintattico.typescript-gradle-plugin")
    id("com.diffplug.spotless") version "6.25.0"
}

typescript {
    entrypoint = "index.js"
}