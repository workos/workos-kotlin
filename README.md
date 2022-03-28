# WorkOS Kotlin [![Maven Central](https://img.shields.io/maven-central/v/com.workos/workos.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.workos%22%20AND%20a:%22workos%22)

The WorkOS Kotlin library provides convenient access to the WorkOS API from applications written in JVM compatible languages.

## Documentation

See the [API Reference](https://workos.com/docs/reference/client-libraries) for Kotlin/Java usage examples.

## Installation

### Apache Maven

```xml
<dependency>
  <groupId>com.workos</groupId>
  <artifactId>workos</artifactId>
  <version>VERSION</version>
</dependency>
```
Customers using Maven who receive a "NoSuchMethodError" error referencing Fuel may need to specify a version of Kotlin greater than 1.4.0. 
```xml
<properties>
    <kotlin.version>1.4.0</kotlin.version>
</properties>
```

### Gradle (Groovy DSL)

```groovy
dependencies {
  implementation 'com.workos:workos:VERSION'
}
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
  implementation("com.workos:workos:VERSION")
}
```

## Use

```java
import com.workos.WorkOS;

WorkOS workos = new WorkOS("WORKOS_API_KEY");

// Access different domains of the WorkOS API through the following properties
// workos.directorySync
// workos.organizations
// workos.passwordless
// workos.portal
// workos.sso
// workos.webhooks
```

## SDK Versioning

For our SDKs WorkOS follows a Semantic Versioning ([SemVer](https://semver.org/)) process where all releases will have a version X.Y.Z (like 1.0.0) pattern wherein Z would be a bug fix (e.g., 1.0.1), Y would be a minor release (1.1.0) and X would be a major release (2.0.0). We permit any breaking changes to only be released in major versions and strongly recommend reading changelogs before making any major version upgrades.

See full examples at https://github.com/workos-inc/java-example-applications.

## More Information

- [Single Sign-On Guide](https://workos.com/docs/sso/guide)
- [Directory Sync Guide](https://workos.com/docs/directory-sync/guide)
- [Admin Portal Guide](https://workos.com/docs/admin-portal/guide)
- [Magic Link Guide](https://workos.com/docs/magic-link/guide)
