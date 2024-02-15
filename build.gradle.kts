plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
}

group = "com.urosjarc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val jaxb_api_version = "2.3.0"
    implementation("javax.xml.bind:jaxb-api:$jaxb_api_version")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxb_api_version")
    implementation("javax.activation:javax.activation-api:1.2.0")

    val ktor_version = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.apache.logging.log4j:log4j-core:2.20.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}
