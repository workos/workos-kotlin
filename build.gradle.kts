import java.io.FileOutputStream
import java.util.Properties

group = "com.workos"
version = "2.1.1"

if (!project.hasProperty("release")) {
  version = "$version-SNAPSHOT"
}

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.0"

  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"

  id("org.jetbrains.dokka") version "1.5.30"

  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"

  signing

  `java-library`

  `maven-publish`
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

  implementation("org.apache.httpcomponents:httpclient:4.2.3")

  implementation("com.github.kittinunf.fuel:fuel:2.3.1")

  testImplementation("org.jetbrains.kotlin:kotlin-test")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")

  testImplementation("com.github.tomakehurst:wiremock:2.27.2")

  dokkaGfmPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.30")

  api("org.apache.commons:commons-math3:3.6.1")
}

val generatedVersionDir = "$buildDir/generated-version"

sourceSets {
  main {
    kotlin {
      output.dir(generatedVersionDir)
    }
  }
}

tasks.register("generateVersionProperties") {
  doLast {
    val propertiesFile = file("$generatedVersionDir/version.properties")
    propertiesFile.parentFile.mkdirs()
    val properties = Properties()
    properties.setProperty("version", "$version")
    val out = FileOutputStream(propertiesFile)
    properties.store(out, null)
  }
}

tasks.named("processResources") {
  dependsOn("generateVersionProperties")
}

tasks.named("build") {
  finalizedBy("dokkaJavadoc")
}

tasks.dokkaJavadoc.configure {
  outputDirectory.set(buildDir.resolve("docs/javadoc"))
  dokkaSourceSets {
    configureEach {
      reportUndocumented.set(true)
    }
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
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = "workos"

      from(components["java"])

      pom {
        name.set("WorkOS SDK")
        description.set("The WorkOS Kotlin library provides convenient access to the WorkOS API from applications written in JVM compatible languages.")
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
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
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

java {
  withSourcesJar()
  withJavadocJar()
}
