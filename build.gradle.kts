import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.urosjarc"
version = "0.0.1-SNAPSHOT"

application {
    mainClass = "com.urosjarc.slotraffic.MapVisualizer"
}

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "2.3.8"

    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    testImplementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    testImplementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("io.ktor:ktor-server-cors:$ktor_version")
    testImplementation("io.ktor:ktor-server-call-logging:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.apache.logging.log4j:log4j-core:2.20.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
}

testlogger {
    this.setTheme("mocha")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.urosjarc.slotraffic.MapVisualizer"
            )
        )
    }
    archiveClassifier.set("test")
    from(sourceSets["test"].output)
    configurations = listOf(project.configurations.testRuntimeClasspath.get())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group as String
            artifactId = rootProject.name
            version = rootProject.version as String
            from(components["java"])

            pom {
                name = "Slo. Traffic"
                description = "Kotlin API to get Slovenian traffic and road information."
                url = "https://github.com/urosjarc/slo-traffic"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "urosjarc"
                        name = "Uroš Jarc"
                        email = "jar.fmf@gmail.com"
                    }
                }
            }
        }
    }
}
