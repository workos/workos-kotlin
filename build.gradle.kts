import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.Properties

group = "com.workos"
version = "5.0.1" // x-release-please-version

if (!project.hasProperty("release")) {
  version = "$version-SNAPSHOT"
}

plugins {
  id("org.jetbrains.kotlin.jvm") version "2.3.21"

  id("org.jlleitschuh.gradle.ktlint") version "14.2.0"

  id("org.jetbrains.dokka") version "2.2.0"

  id("org.jetbrains.dokka-javadoc") version "2.2.0"

  id("com.vanniktech.maven.publish") version "0.36.0"

  `java-library`
}

repositories {
  mavenCentral()
  mavenLocal()
}

val kotlinVersion = getKotlinPluginVersion()

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.2")

  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // JWT verification + JWKS handling for session helpers (hand-maintained).
  implementation("com.nimbusds:nimbus-jose-jwt:9.41.2")

  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

  testImplementation(platform("org.junit:junit-bom:6.0.3"))

  testImplementation(kotlin("test-junit5"))

  testImplementation("org.junit.jupiter:junit-jupiter-api")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  testImplementation("org.wiremock:wiremock:3.13.2")
}

val generatedVersionDir = layout.buildDirectory.dir("generated-version")

sourceSets {
  main {
    kotlin {
      output.dir(generatedVersionDir)
    }
  }
}

tasks.register("generateVersionProperties") {
  doLast {
    val propertiesFile = generatedVersionDir.get().file("version.properties").asFile
    propertiesFile.parentFile.mkdirs()
    val properties = Properties()
    properties.setProperty("version", "$version")
    propertiesFile.outputStream().use { out ->
      properties.store(out, null)
    }
  }
}

tasks.named("processResources") {
  dependsOn("generateVersionProperties")
}

tasks.named("build") {
  finalizedBy("dokkaGeneratePublicationJavadoc")
}

tasks.named("javadoc") {
  finalizedBy("dokkaGeneratePublicationJavadoc")
}

dokka {
  dokkaPublications.javadoc {
    outputDirectory.set(layout.buildDirectory.dir("docs/javadoc"))
    failOnWarning.set(true)
  }

  dokkaSourceSets.configureEach {
    reportUndocumented.set(true)
  }
}

tasks.withType<DokkaTask>().configureEach {
  failOnWarning.set(true)
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

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  coordinates("com.workos", "workos", version.toString())

  pom {
    name.set("WorkOS SDK")
    description.set(
      "The WorkOS Kotlin library provides convenient access to the WorkOS API " +
        "from applications written in JVM compatible languages."
    )
    url.set("https://github.com/workos/workos-kotlin")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://github.com/workos/workos-kotlin/blob/main/LICENSE")
      }
    }
    developers {
      developer {
        id.set("workos")
        name.set("WorkOS")
        email.set("sdk@workos.com")
        organization.set("WorkOS")
        organizationUrl.set("https://workos.com")
      }
    }
    scm {
      connection.set("scm:git:git://github.com/workos/workos-kotlin.git")
      developerConnection.set("scm:git:git@github.com:workos/workos-kotlin.git")
      url.set("https://github.com/workos/workos-kotlin")
    }
  }
}
