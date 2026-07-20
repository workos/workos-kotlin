# WorkOS Kotlin [![Maven Central](https://img.shields.io/maven-central/v/com.workos/workos.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.workos%22%20AND%20a:%22workos%22)

The WorkOS Kotlin library provides convenient access to the WorkOS API from applications written in JVM-compatible languages (Kotlin, Java, Scala, etc.).

## Documentation

See the [API Reference](https://workos.com/docs/reference/client-libraries) for Kotlin/Java usage examples.
See the [v5 migration guide](./docs/V5_MIGRATION_GUIDE.md) when upgrading from the pre-generated SDK surface.

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

- JDK 17+
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

Service accessors are regular properties on the `WorkOS` class, so they work natively from Java:

```java
import com.workos.WorkOS;

WorkOS workos = new WorkOS(System.getenv("WORKOS_API_KEY"));
workos.getUserManagement().list();
```

For more configurable construction, `WorkOS.builder()` exposes a fluent
builder so Java callers can set only the parameters they care about:

```java
import com.workos.WorkOS;
import com.workos.common.http.RetryConfig;

WorkOS workos = WorkOS.builder()
    .apiKey(System.getenv("WORKOS_API_KEY"))
    .clientId(System.getenv("WORKOS_CLIENT_ID"))
    .retryConfig(RetryConfig.DISABLED)
    .build();
```

Exception and client-configuration properties are exposed with `@JvmField`,
so Java callers read them as fields (`e.code`, `e.rawBody`). Generated model
properties are regular Kotlin `val`s — from Java, use the bean-style getters
(`connection.getId()`).
Sealed-class operation parameters (e.g. `ResourceTarget`) include
Java-friendly overloads that take the discriminating fields directly
(`workos.getAuthorization().checkByExternalId(...)`) so you do not have to
construct the variant class explicitly.

## Service accessors

Once constructed, every WorkOS API surface is available as a property on the `WorkOS` instance.

### Authentication and identity

- `workos.sso`
- `workos.userManagement`
- `workos.passwordless` †
- `workos.multiFactorAuth`
- `workos.session` †
- `workos.pkce` †

### Organizations and directories

- `workos.organizations`
- `workos.organizationDomains`
- `workos.organizationMembership`
- `workos.directorySync`
- `workos.groups`

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
- `workos.pipesProvider`
- `workos.widgets`
- `workos.apiKeys`
- `workos.adminPortal`
- `workos.clientApi`
- `workos.actions` †
- `workos.vault`

† Provided as a Kotlin extension property. From Java, access these via the
generated static accessor on the corresponding `*Kt` file
(e.g. `PasswordlessKt.getPasswordless(workos)`) instead of as a member of
`WorkOS`.

## Quickstart examples

```kotlin
import com.workos.WorkOS

val workos = WorkOS(System.getenv("WORKOS_API_KEY"))

val org = workos.organizations.create(
  name = "Foo Corp",
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
| other       | `com.workos.common.exceptions.WorkOSGenericException`       |
| transport   | `com.workos.common.exceptions.TransportException`           |

`TransportException` (status `0`) is thrown when the SDK cannot reach the
API at all — IOExceptions, timeouts, DNS or TLS failures.
`WorkOSGenericException` is thrown when the API returns an HTTP status the
SDK does not have a more specific exception type for.

Each exception exposes the response `code`, human-readable `message`, and
the structured `errors: List<ApiError>?` array returned by the API.
`ApiError` is a typed model (`field`, `code`, `message`) so callers can
inspect validation failures without `Map` casts.
`RateLimitException` additionally exposes `retryAfterSeconds` parsed from
the `Retry-After` response header (delta-seconds or HTTP-date) when the
server provides one. `WorkOSException.rawBody` also preserves the raw HTTP
response body for debugging; treat it as sensitive because it can contain
PII or secrets that should be redacted before logging.

```kotlin
import com.workos.common.exceptions.NotFoundException

try {
  workos.organizations.get("org_nope")
} catch (e: NotFoundException) {
  println("Not found: ${e.message}")
}
```

## Webhook signatures

The standalone `Webhook` helper validates signatures on inbound webhook
payloads. Two flavors are available:

- `constructTypedEvent(...)` returns a `WorkOSEvent` (a sealed interface
  whose implementations are top-level types in `com.workos.models`) you can
  pattern-match against — preferred for the common case.
- `constructEvent(...)` returns a raw `JsonNode`, which is useful if you
  need to handle event types the SDK does not yet model.

```kotlin
import com.workos.models.UserCreated
import com.workos.webhooks.Webhook

val event = Webhook().constructTypedEvent(
  payload = rawRequestBody,
  signatureHeader = request.getHeader("WorkOS-Signature"),
  secret = System.getenv("WORKOS_WEBHOOK_SECRET"),
)

when (event) {
  is UserCreated -> println("New user ${event.data.id}")
  else -> println("Other event ${event.event}")
}
```

## PATCH semantics

PATCH endpoints distinguish between "field omitted", "field set to a
value", and "field explicitly cleared to `null`". Use `PatchField` to
express that intent — see the
[v5 migration guide](./docs/V5_MIGRATION_GUIDE.md#8-patch-semantics-use-patchfield)
for examples.

## Coroutines

Every generated service method has both a blocking version and a
coroutine-aware `<method>Suspend` variant that wraps the call in
`withContext(Dispatchers.IO)`. Add `kotlinx-coroutines-core` to your
classpath (the SDK already pulls it in at runtime, but declare it yourself
for compilation) and call from any `suspend` context:

```kotlin
val org = workos.organizations.createSuspend(name = "Foo Corp")

val firstPage = workos.organizations.listSuspend(limit = 10)
for (org in firstPage.data) println(org.id)
```

The blocking and suspend forms are independently named (`create` vs
`createSuspend`) so they can coexist on the same service without Kotlin
overload-resolution ambiguity.

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

workos.organizations.create(
  name = "Foo Corp",
  requestOptions =
    RequestOptions.builder()
      .maxRetries(0)
      .build(),
)
```

> [!NOTE]
> `RequestOptions` also accepts an `idempotencyKey`, which is sent as the `Idempotency-Key` header. When retries are enabled the SDK also automatically attaches a generated `Idempotency-Key` to every `POST` that lacks one. The WorkOS API currently honors the key only on the [Create Audit Log Event](https://workos.com/docs/reference/audit-logs/event) endpoint (`auditLogs.createEvent`). Other endpoints accept the header but do not deduplicate requests, so a retried mutation elsewhere can still create a duplicate.

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
- [Example applications](https://github.com/workos/java-example-applications) (archived)
