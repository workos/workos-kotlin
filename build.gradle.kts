import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

group = "com.workos"
version = "4.25.0" // x-release-please-version

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

dependencies {
  implementation(platform(kotlin("bom")))

  implementation(kotlin("stdlib"))

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")

  implementation("org.apache.httpcomponents:httpclient:4.5.14")

  implementation("com.github.kittinunf.fuel:fuel:2.3.1")

  implementation(kotlin("reflect"))

  testImplementation(platform("org.junit:junit-bom:6.0.3"))

  testImplementation(kotlin("test-junit5"))

  testImplementation("org.junit.jupiter:junit-jupiter-api")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  testImplementation("org.wiremock:wiremock:3.13.2")

  api("org.apache.commons:commons-math3:3.6.1")
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
  }

  dokkaSourceSets.configureEach {
    reportUndocumented.set(true)
  }
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
    url.set("https://github.com/workos-inc/workos-kotlin")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://github.com/workos-inc/workos-kotlin/blob/main/LICENSE")
      }
    }
    developers {
      developer {
        id.set("rframpton")
        name.set("Robert Frampton")
        email.set("rob@workos.com")
      }
      developer {
        id.set("dliu-workos")
        name.set("David Liu")
        email.set("david.liu@workos.com")
      }
    }
    scm {
      connection.set("scm:git:git://github.com/workos-inc/workos-kotlin.git")
      developerConnection.set("scm:git:git@github.com:workos-inc/workos-kotlin.git")
      url.set("https://github.com/workos-inc/workos-kotlin")
    }
  }
}
