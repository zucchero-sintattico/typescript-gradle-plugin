plugins {
    id("io.github.zucchero-sintattico.typescript-gradle-plugin")
}

node {
    shouldInstall.set(true)
    version.set("20.7.0")
}
