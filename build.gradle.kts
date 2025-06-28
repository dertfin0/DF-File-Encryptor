plugins {
    id("java")
    id("application")
}

group = "ru.dfhub.dfe"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.dfhub.dfe.Main"
    }
}