import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.jetbrains.kotlin.jvm")

  id("org.jlleitschuh.gradle.ktlint")

  id("org.jetbrains.dokka")

  id("org.jetbrains.dokka-javadoc")

  id("com.vanniktech.maven.publish")

  `java-library`
}

group = "com.workos"
version = "0.0.0" // x-release-please-version

if (!project.hasProperty("release")) {
  version = "$version-SNAPSHOT"
}

repositories {
  mavenCentral()
}

// The SDK is declared by its published coordinates with a capped version
// range, so the POM and the Gradle module metadata carry the range and not
// the exact SDK version this module happened to be built with. Inside this
// build the range is satisfied by the SDK project itself.
configurations.configureEach {
  resolutionStrategy.dependencySubstitution {
    substitute(module("com.workos:workos")).using(project(":"))
  }
}

dependencies {
  // Needed only for the IdentityTokenProvider interface. The lower bound is
  // the first SDK release that has it; raise the upper bound with every SDK
  // major.
  api("com.workos:workos:[7.7.0,8.0.0)")

  // The only place the AWS SDK appears. It is never a dependency of the SDK.
  implementation("software.amazon.awssdk:sts:2.57.13")

  testImplementation(platform("org.junit:junit-bom:6.1.3"))

  testImplementation(kotlin("test-junit5"))

  testImplementation("org.junit.jupiter:junit-jupiter-api")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dokka {
  dokkaPublications.configureEach {
    failOnWarning.set(true)
  }

  dokkaSourceSets.configureEach {
    reportUndocumented.set(true)
  }
}

tasks.jar {
  manifest {
    attributes(
      mapOf(
        "Implementation-Title" to "workos-workload-identity-aws",
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

  coordinates("com.workos", "workos-workload-identity-aws", version.toString())

  pom {
    name.set("WorkOS workload identity for AWS")
    description.set(
      "Identity token provider that lets the WorkOS Kotlin SDK authenticate from AWS without an API key."
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
