version = "0.0.1"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"

    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"

    `java-library`
}

repositories { mavenCentral() }

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("org.apache.httpcomponents:httpclient:4.2.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    api("org.apache.commons:commons-math3:3.6.1")
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

java { withSourcesJar() }
