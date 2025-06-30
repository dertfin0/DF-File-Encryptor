plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.0.0-beta17"
}

group = "ru.dfhub.dfe"
version = "2.0"

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

tasks.build {
    dependsOn(tasks.shadowJar)
}

setProperty("mainClassName", "ru.dfhub.dfe.Main")