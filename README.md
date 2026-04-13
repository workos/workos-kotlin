# WorkOS Kotlin [![Maven Central](https://img.shields.io/maven-central/v/com.workos/workos.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.workos%22%20AND%20a:%22workos%22)

The WorkOS Kotlin library provides convenient access to the WorkOS API from applications written in JVM-compatible languages (Kotlin, Java, Scala, etc.).

## Documentation

See the [API Reference](https://workos.com/docs/reference/client-libraries) for Kotlin/Java usage examples.

## Installation

Replace `VERSION` in the snippets below with the latest release from [Maven Central](https://search.maven.org/artifact/com.workos/workos).

### Apache Maven

```xml
<dependency>
  <groupId>com.workos</groupId>
  <artifactId>workos</artifactId>
  <version>VERSION</version>
</dependency>
```

Maven users who see a `NoSuchMethodError` referencing Fuel may need to pin Kotlin to `2.1.21` or newer:

```xml
<properties>
    <kotlin.version>2.1.21</kotlin.version>
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

## Compatibility

- JDK 11+
- Kotlin 2.1.x or newer

## Configuration

Construct a `WorkOS` client by passing your API key. All other parameters are optional.

```kotlin
import com.workos.WorkOS
import com.workos.common.http.RetryConfig

val workos = WorkOS(
  apiKey = System.getenv("WORKOS_API_KEY"),
  // Required for several user-management auth flows (token exchange, etc.)
  clientId = System.getenv("WORKOS_CLIENT_ID"),
  // Defaults to "https://api.workos.com". Override for staging or proxies.
  apiBaseUrl = "https://api.workos.com",
  // Optional — defaults to a 30s-timeout OkHttpClient.
  // httpClient = myCustomOkHttpClient,
  // Retry/backoff policy. Defaults to exponential backoff with 3 retries.
  retryConfig = RetryConfig.DEFAULT,
)
```

| Parameter     | Required | Description                                                       |
| ------------- | -------- | ----------------------------------------------------------------- |
| `apiKey`      | yes      | WorkOS secret key (`sk_*`).                                       |
| `clientId`    | no       | Required for user-management auth flows and public-client usage.  |
| `apiBaseUrl`  | no       | Defaults to `https://api.workos.com`. Override for staging/local. |
| `httpClient`  | no       | Bring-your-own `OkHttpClient` for telemetry, interceptors, etc.   |
| `retryConfig` | no       | See `RetryConfig.DEFAULT` / `RetryConfig.DISABLED`.               |

### Java usage

Service accessors are exposed as Kotlin extension properties on `WorkOS`.
In Java, call the generated accessor classes directly:

```java
import com.workos.WorkOS;
import com.workos.usermanagement.UserManagement;

WorkOS workos = new WorkOS(System.getenv("WORKOS_API_KEY"));

// Kotlin extension properties are exposed to Java as top-level static
// getters on WorkOSGeneratedKt:
UserManagement userManagement =
  com.workos.WorkOSGeneratedKt.getUserManagement(workos);
```

## Service accessors

Once constructed, every WorkOS API surface is available as an extension property on the `WorkOS` instance.

### Authentication and identity

- `workos.sso`
- `workos.userManagement`
- `workos.passwordless`
- `workos.multiFactorAuth`

### Organizations and directories

- `workos.organizations`
- `workos.organizationDomains`
- `workos.directorySync`

### Authorization and access control

- `workos.authorization`
- `workos.featureFlags`

### Observability and compliance

- `workos.auditLogs`
- `workos.events`
- `workos.webhooks`
- `workos.radar`

### Integrations and tooling

- `workos.connect`
- `workos.pipes`
- `workos.widgets`
- `workos.apiKeys`
- `workos.adminPortal`

## Quickstart examples

```kotlin
import com.workos.WorkOS
import com.workos.organizations.organizations

val workos = WorkOS(System.getenv("WORKOS_API_KEY"))

val org = workos.organizations.createOrganization(
  name = "Foo Corp",
  domainData = emptyList(),
)
println("Created ${org.id}")

// Auto-paginate through every event, one page at a time.
val events = workos.events.list(events = listOf("dsync.user.created"))
for (event in events.autoPagingIterable()) {
  println(event.id)
}
```

## Error handling

Every API error deserializes into a typed subclass of
`com.workos.common.exceptions.WorkOSException`:

| HTTP status | Exception                                                   |
| ----------- | ----------------------------------------------------------- |
| 400         | `com.workos.common.exceptions.BadRequestException`          |
| 401         | `com.workos.common.exceptions.UnauthorizedException`        |
| 404         | `com.workos.common.exceptions.NotFoundException`            |
| 422         | `com.workos.common.exceptions.UnprocessableEntityException` |
| 429         | `com.workos.common.exceptions.RateLimitException`           |
| 5xx         | `com.workos.common.exceptions.GenericServerException`       |
| other       | `com.workos.common.exceptions.GenericException`             |

Each exception exposes the response `code`, human-readable `message`, and
any structured validation `errors` returned by the API.
`RateLimitException` additionally exposes `retryAfterSeconds` parsed from
the `Retry-After` response header (delta-seconds or HTTP-date) when the
server provides one.

```kotlin
import com.workos.common.exceptions.NotFoundException

try {
  workos.organizations.getOrganization("org_nope")
} catch (e: NotFoundException) {
  println("Not found: ${e.message}")
}
```

## Webhook signatures

`workos.webhooks` ships a Webhook helper for validating signatures on
inbound webhook payloads.

```kotlin
val event = workos.webhooks.constructEvent(
  payload = rawRequestBody,
  sigHeader = request.getHeader("WorkOS-Signature"),
  secret = System.getenv("WORKOS_WEBHOOK_SECRET"),
)
```

## Pagination

All `list(...)` methods return a `Page<T>`. You can either consume a single
page directly, or walk every page via the auto-paging iterator.

```kotlin
val firstPage = workos.organizations.list(limit = 10)
for (org in firstPage.data) println(org.id)

// Walk the full result set.
for (org in firstPage.autoPagingIterable()) println(org.id)
```

## Per-request options

Every generated method accepts an optional `RequestOptions` argument for
per-call overrides:

```kotlin
import com.workos.common.http.RequestOptions

workos.organizations.createOrganization(
  name = "Foo Corp",
  domainData = emptyList(),
  requestOptions = RequestOptions(
    idempotencyKey = java.util.UUID.randomUUID().toString(),
    maxRetries = 0,
  ),
)
```

## Unknown enum values

Generated enums include an `Unknown` variant annotated with Jackson's
`@JsonEnumDefaultValue`. When the API introduces a new wire value the SDK
has not yet been updated for, deserialization falls back to `Unknown`
instead of throwing. Callers that need to preserve the raw value should
inspect the underlying JSON directly until the SDK is regenerated.

## SDK versioning

This SDK follows [SemVer](https://semver.org/). Breaking changes only ship
in major releases — read the changelog before upgrading across a major
version boundary.

## More information

- [Single Sign-On Guide](https://workos.com/docs/sso/guide)
- [Directory Sync Guide](https://workos.com/docs/directory-sync/guide)
- [User Management](https://workos.com/docs/user-management/guide)
- [Admin Portal Guide](https://workos.com/docs/admin-portal/guide)
- [Magic Link Guide](https://workos.com/docs/magic-link/guide)
- [Example applications](https://github.com/workos-inc/java-example-applications)
