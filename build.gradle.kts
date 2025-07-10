plugins {
    id("java")
}

group = "me.icanttellyou"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://repo.unascribed.com")
        content {
            includeGroup("com.unascribed")
        }
    }

    mavenCentral()
}

val EARS_VERSION = "1.4.7"

dependencies {
    implementation("com.unascribed:ears-api:${EARS_VERSION}")
    implementation("com.unascribed:ears-common:${EARS_VERSION}:legacy")

    implementation("com.google.code.gson:gson:2.13.1")
    implementation("org.joml:joml:1.10.8")
}