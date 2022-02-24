plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("dependencies") {
            id = "io.github.persiancalendar.dependencies"
            implementationClass = "io.github.persiancalendar.gradle.DependenciesPlugin"
        }
    }
}
