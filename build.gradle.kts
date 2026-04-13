import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

group = "com.workos"
version = "4.22.0" // x-release-please-version

if (!project.hasProperty("release")) {
  version = "$version-SNAPSHOT"
}

plugins {
  id("org.jetbrains.kotlin.jvm") version "2.1.21"

  id("org.jlleitschuh.gradle.ktlint") version "14.2.0"

  id("org.jetbrains.dokka") version "2.2.0"

  id("org.jetbrains.dokka-javadoc") version "2.2.0"

  id("io.github.gradle-nexus.publish-plugin") version "2.0.0"

  signing

  `java-library`

  `maven-publish`
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(platform(kotlin("bom")))

  implementation(kotlin("stdlib"))

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.2")

  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  implementation("commons-codec:commons-codec:1.17.1")

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

  withSourcesJar()
  withJavadocJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = "workos"

      from(components["java"])

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
  }
}

nexusPublishing {
  repositories {
    create("myNexus") {
      // Central Portal compatibility endpoints (post-OSSRH EOL June 30, 2025)
      nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
      snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
    }
  }
}

signing {
  val signingPassword: String? by project

  if (signingPassword != null) {
    val signingKey = File("signing.key").readText()

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
  }
}
