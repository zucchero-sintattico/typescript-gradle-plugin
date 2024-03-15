# Typescript Gradle Plugin

<!--
1. Rename the project in `settings.gradle.kts`
1. Change the project information in `build.gradle.kts`
1. Change the username for Maven Central in `.github/workflows/build-and-deploy.yml`
1. Configure the CI to have the following secrets set:
  * GRADLE_PUBLISH_KEY
  * GRADLE_PUBLISH_SECRET
  * MAVEN_CENTRAL_PASSWORD
  * SIGNING_KEY
-->

Gradle plugin to build typescript and produce  `.js` file.

It makes the assumption that the typescript files are in `src/main/typescript`.

## Usage

Just apply it to your project:

```kotlin
plugins {
    id("io.github.zucchero-sintattico.typescript-gradle-plugin") version "<latest version>"
}
```

At the moment there are the following tasks:

- `CheckNode`: checks if node is installed
- `compileTypescript`: compiles the typescript file
