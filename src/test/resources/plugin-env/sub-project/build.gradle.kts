plugins {
    id("io.github.zucchero-sintattico.typescript-gradle-plugin")
}

typescript {
    entrypoint = "index.js"
}

project {
    basePath = "project/"
}