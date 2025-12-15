plugins {
    id("java-library")
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.jetbrainsKotlinJvm)
}

group = "net.cacheux.bytonio"
version = "0.0.3"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(project(":core"))
    implementation(libs.ksp.symbolProcessing)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(kotlin("reflect"))
    testImplementation(libs.junit)
    testImplementation(libs.compiletesting)
    testImplementation(libs.compiletesting.ksp)
    testImplementation(libs.mockk)
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "bytonio-processor", version.toString())

    pom {
        name = "Bytonio Processor"
        description = "Generate serializers and deserializers for binary formats"
        url = "https://github.com/lcacheux/bytonio"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "lcacheux"
                name = "Leo Cacheux"
                email = "leo@cacheux.net"
            }
        }
        scm {
            connection = "scm:git:https://github.com/lcacheux/bytonio.git"
            developerConnection = "scm:git:ssh://github.com/lcacheux/bytonio.git"
            url = "https://github.com/lcacheux/bytonio"
        }
    }
}
