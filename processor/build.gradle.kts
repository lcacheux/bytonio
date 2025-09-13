plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jetbrainsKotlinJvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
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

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("release"))
        }
    }

    publications {
        create<MavenPublication>("mavenPublish") {
            groupId = "net.cacheux.bytonio"
            artifactId = "bytonio-processor"
            version = "0.0.1"
            afterEvaluate {
                from(components["kotlin"])
            }

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
    }
}
