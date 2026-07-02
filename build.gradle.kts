import org.jetbrains.dokka.gradle.formats.DokkaFormatPlugin
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.Properties

group = "com.workos"
version = "6.4.0" // x-release-please-version

if (!project.hasProperty("release")) {
  version = "$version-SNAPSHOT"
}

plugins {
  id("org.jetbrains.kotlin.jvm") version "2.4.0"

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

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.0")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0")

  implementation("com.squareup.okhttp3:okhttp:5.4.0")

  // JWT verification + JWKS handling for session helpers (hand-maintained).
  implementation("com.nimbusds:nimbus-jose-jwt:10.9.1")

  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

  testImplementation(platform("org.junit:junit-bom:6.1.0"))

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
  }

  dokkaPublications.configureEach {
    failOnWarning.set(true)
  }

  dokkaSourceSets.configureEach {
    reportUndocumented.set(true)
  }
}

// GFM (GitHub-Flavored Markdown) output via the official DokkaFormatPlugin
// extension point. DGP v2 doesn't ship a built-in markdown format, but the
// gfm-plugin artifacts are still published, and JetBrains documents this
// pattern as the supported way to wire them in. Produces a `dokkaGenerateMarkdown`
// task whose output lands in build/dokka/markdown/.
@OptIn(InternalDokkaGradlePluginApi::class)
abstract class DokkaMarkdownPlugin : DokkaFormatPlugin(formatName = "markdown") {
  override fun DokkaFormatPlugin.DokkaFormatPluginContext.configure() {
    project.dependencies {
      dokkaPlugin(dokka("gfm-plugin"))
      formatDependencies
        .dokkaPublicationPluginClasspathApiOnly
        .dependencies
        .addLater(dokka("gfm-template-processing-plugin"))
    }
  }
}

apply<DokkaMarkdownPlugin>()

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
