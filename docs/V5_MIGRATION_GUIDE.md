# V5 Migration Guide

This guide covers the upgrade from the v4 Kotlin SDK surface to the v5 surface.

v5 is a substantial SDK rewrite:

- the transport layer moved from Fuel to OkHttp
- service classes were regenerated from the OpenAPI spec
- models and enums were consolidated into shared packages
- most builder-style request objects were replaced with flat method arguments or small option data classes
- pagination, retries, request overrides, and error handling were standardized across the SDK

If your code imports SDK models directly, constructs request builders, or uses webhook verification, expect source changes.

## Upgrade Checklist

- Upgrade your runtime to Java 17 before adopting v5.
- Recreate `WorkOS` clients with constructor arguments instead of mutating `apiHostname` / `https` / `port`.
- Move any per-call `clientId` arguments into the `WorkOS` client configuration.
- Replace `*Api` classes, per-service `models` packages, per-service `types` packages, and builder classes.
- Update list handling to use `Page<T>` and `autoPagingIterable()`.
- Replace webhook signature verification calls with the standalone `Webhook` helper.
- Switch any Fuel customization to `OkHttpClient`.

## 1. Runtime And Transport Changes

v4 was built around Fuel and older Kotlin/JVM targets. v5 now builds around OkHttp and targets Java 17 bytecode.

What changes:

- Java 17 is now required for consumers.
- Kotlin consumers should align to Kotlin 2.1.x or newer.
- Any code that depended on Fuel types, Fuel interceptors, or `FuelManager` needs to move to `OkHttpClient`.

Before:

```kotlin
val workos = WorkOS(System.getenv("WORKOS_API_KEY"))
```

After:

```kotlin
val workos = WorkOS(
  apiKey = System.getenv("WORKOS_API_KEY"),
  clientId = System.getenv("WORKOS_CLIENT_ID"), // optional unless your flow needs it
  apiBaseUrl = "https://api.workos.com",
  httpClient = okhttp3.OkHttpClient(),
  retryConfig = com.workos.common.http.RetryConfig.DEFAULT,
)
```

## 2. Client Configuration Is Immutable

In v4, the client was configured after construction:

```kotlin
val workos = WorkOS(apiKey)
workos.apiHostname = "localhost"
workos.https = false
workos.port = 8000
```

In v5, configuration is constructor-based:

```kotlin
val workos = WorkOS(
  apiKey = apiKey,
  apiBaseUrl = "http://localhost:8000",
)
```

Also note:

- `clientId` now lives on the client and is reused by User Management and SSO helpers.
- low-level `workos.get(...)`, `post(...)`, `put(...)`, `patch(...)`, and `delete(...)` helpers are gone
- if you were using those internals directly, move to service methods or `workos.baseClient.request(...)`

## 3. Service Accessor Changes

Most `*Api` classes lost the `Api` suffix, and some service names changed.

| v4                              | v5                                                                                       |
| ------------------------------- | ---------------------------------------------------------------------------------------- |
| `OrganizationsApi`              | `Organizations`                                                                          |
| `DirectorySyncApi`              | `DirectorySync`                                                                          |
| `UserManagementApi`             | `UserManagement`                                                                         |
| `SsoApi`                        | `SSO`                                                                                    |
| `AuditLogsApi`                  | `AuditLogs`                                                                              |
| `WidgetsApi`                    | `Widgets`                                                                                |
| `PortalApi` via `workos.portal` | `AdminPortal` via `workos.adminPortal`                                                   |
| `MfaApi` via `workos.mfa`       | `MultiFactorAuth` via `workos.multiFactorAuth`                                           |
| `FgaApi` via `workos.fga`       | folded into `Authorization` via `workos.authorization`                                   |
| `WebhooksApi`                   | `Webhooks` for endpoint management, plus standalone `Webhook` for signature verification |

Service accessors are now generated as properties directly on the `WorkOS` class.

Before:

```kotlin
val org = workos.organizations.getOrganization("org_123")
val link = workos.portal.generateLink(...)
val factor = workos.mfa.getFactor("auth_factor_123")
```

After:

```kotlin
val org = workos.organizations.get("org_123")
val link = workos.adminPortal.generateLink(...)
val factor = workos.multiFactorAuth.getFactor("auth_factor_123")
```

### Java Callers

Service accessors work natively from Java — no extra wrapper class needed:

```java
WorkOS workos = new WorkOS(System.getenv("WORKOS_API_KEY"), System.getenv("WORKOS_CLIENT_ID"));
Organizations organizations = workos.getOrganizations();
```

## 4. Packages Were Consolidated

v4 split request/response types across many service-local packages:

- `com.workos.organizations.models.*`
- `com.workos.usermanagement.types.*`
- `com.workos.fga.builders.*`
- `com.workos.webhooks.models.*`

v5 consolidates most generated types into:

- `com.workos.models.*`
- `com.workos.types.*`

Examples:

| v4 import                                      | v5 import                        |
| ---------------------------------------------- | -------------------------------- |
| `com.workos.organizations.models.Organization` | `com.workos.models.Organization` |
| `com.workos.sso.models.Connection`             | `com.workos.models.Connection`   |
| `com.workos.common.models.Order`               | `com.workos.types.EventsOrder`   |

Also:

- most `builders` packages are gone
- many `types` classes used only to shape request bodies are gone
- webhook event classes are no longer under `com.workos.webhooks.models`

## 5. Request Builders Were Replaced

v4 leaned heavily on builders and service-specific option objects.

Before:

```kotlin
val org = workos.organizations.createOrganization(
  OrganizationsApi.CreateOrganizationOptions.builder()
    .name("Acme")
    .domains(listOf("acme.com"))
    .build()
)
```

After:

```kotlin
val org = workos.organizations.create(
  name = "Acme",
  domains = listOf("acme.com"),
)
```

Before:

```kotlin
val users = workos.userManagement.listUsers(
  ListUsersOptions.builder()
    .email("admin@acme.com")
    .build()
)
```

After:

```kotlin
val users = workos.userManagement.list(
  email = "admin@acme.com",
)
```

The general rule in v5 is:

- simple operations use flat method arguments
- URL-building helpers use small option data classes like `AuthKitAuthorizationUrlOptions` and `SSOAuthorizationUrlOptions`
- per-request behavior uses a shared `RequestOptions` builder

## 6. `clientId` Moved To The Client

Several v4 User Management and SSO methods required `clientId` on every call.

Before:

```kotlin
val auth = workos.userManagement.authenticateWithPassword(
  clientId,
  "user@example.com",
  "secret-password",
)
```

After:

```kotlin
val workos = WorkOS(
  apiKey = apiKey,
  clientId = clientId,
)

val auth = workos.userManagement.authenticateWithPassword(
  email = "user@example.com",
  password = "secret-password",
)
```

The same applies to:

- `authenticateWithCode`
- `authenticateWithRefreshToken`
- `authenticateWithMagicAuth`
- `authenticateWithEmailVerification`
- `authenticateWithTotp`
- AuthKit URL helpers
- SSO URL helpers

## 7. Pagination Is Now Uniform

v4 returned service-specific list wrappers such as `OrganizationList`, `Users`, `ConnectionList`, and `Invitations`.

v5 returns `Page<T>` from every generated `list(...)` method.

Before:

```kotlin
val result = workos.organizations.listOrganizations()
for (org in result.data) {
  println(org.id)
}
```

After:

```kotlin
val page = workos.organizations.list(limit = 10)
for (org in page.data) {
  println(org.id)
}

for (org in page.autoPagingIterable()) {
  println(org.id)
}
```

Important changes:

- `PaginationParams` is gone
- `list(...)` methods accept cursor and filter arguments directly
- cursor metadata now lives on `page.listMetadata`
- auto-pagination is built in via `page.autoPagingIterable()`

## 8. PATCH Semantics Use `PatchField`

PATCH endpoints now distinguish between:

- field omitted
- field set to a value
- field explicitly cleared to `null`

For those endpoints, use `PatchField`.

```kotlin
import com.workos.common.http.PatchField

workos.webhooks.updateEndpoint(
  id = "wh_123",
  endpointUrl = PatchField.of("https://example.com/webhooks"),
  status = PatchField.absent(),
  events = PatchField.ofNull(),
)
```

If you pass plain nullable values where a `PatchField<T>` is required, the call site must be updated.

## 9. Webhook Verification Moved

This is one of the biggest behavior changes.

In v4, webhook signature verification lived on `WebhooksApi`, which was exposed as `workos.webhooks`.

In v5:

- `workos.webhooks` is the API client for webhook endpoint CRUD
- `Webhook()` is the standalone signature verification helper

Before:

```kotlin
val event = workos.webhooks.constructEvent(payload, signatureHeader, secret)
```

After:

```kotlin
val event = com.workos.webhooks.Webhook()
  .constructEvent(payload, signatureHeader, secret)
```

Also note:

- v5 returns a `JsonNode` from `constructEvent(...)`
- if you need a typed event model, deserialize that node into the specific `com.workos.models.*` class you expect
- the helper accepts both `v1=` and legacy `s=` signature formats

## 10. Request Overrides Are Standardized

v4 had scattered request option classes such as `CreateOrganizationRequestOptions`.

v5 uses a shared `RequestOptions` builder:

```kotlin
import com.workos.common.http.RequestOptions

val requestOptions =
  RequestOptions.builder()
    .idempotencyKey(java.util.UUID.randomUUID().toString())
    .header("X-Trace-Id", "trace_123")
    .maxRetries(0)
    .build()

val org = workos.organizations.create(
  name = "Acme",
  requestOptions = requestOptions,
)
```

Supported per-request overrides include:

- headers
- timeout
- max retries
- base URL
- idempotency key
- API key or bearer token override

One subtle SSO difference: if you previously called `sso.getProfile(accessToken)`, in v5 you override the bearer token via `RequestOptions`:

```kotlin
val profile = workos.sso.getProfile(
  requestOptions =
    RequestOptions.builder()
      .apiKey(accessToken)
      .build()
)
```

## 11. Error Handling And Retries Changed

All SDK errors now inherit from `com.workos.common.exceptions.WorkOSException`.

Typed subclasses include:

- `BadRequestException`
- `UnauthorizedException`
- `NotFoundException`
- `UnprocessableEntityException`
- `RateLimitException`
- `GenericServerException`
- `GenericException`

New behavior to be aware of:

- retries are now handled centrally instead of only on specific endpoints
- `Retry-After` is honored for retryable responses
- `RateLimitException` exposes `retryAfterSeconds`
- `WorkOSException` includes `status`, `requestId`, `code`, `errors`, and `rawBody`

If you previously caught generic transport exceptions from Fuel, update those catch blocks to catch `WorkOSException` or the specific typed subclass you care about.

## 12. Public-Client Flows Have A Dedicated Client

If you support browser, mobile, CLI, or desktop PKCE flows, v5 adds `PublicClient`.

```kotlin
val publicClient = PublicClient.create(
  clientId = System.getenv("WORKOS_CLIENT_ID"),
)

val authUrl = publicClient.getAuthorizationUrlWithPKCE(
  com.workos.usermanagement.AuthKitAuthorizationUrlOptions(
    redirectUri = "https://example.com/callback",
    provider = "authkit",
  )
)
```

Use `PublicClient` when you do not want to send a `client_secret` on the wire.

## 13. Common Before/After Rewrites

### Organizations

```kotlin
// v4
val org = workos.organizations.getOrganization("org_123")
val all = workos.organizations.listOrganizations()

// v5
val org = workos.organizations.get("org_123")
val all = workos.organizations.list()
```

### User Management

```kotlin
// v4
val user = workos.userManagement.createUser(
  CreateUserOptions(email = "user@example.com")
)

// v5
val user = workos.userManagement.create(
  email = "user@example.com",
)
```

### AuthKit URL generation

```kotlin
// v4
val url = workos.userManagement
  .getAuthorizationUrl(clientId, "https://example.com/callback")
  .provider("authkit")
  .build()

// v5
val url = workos.userManagement.getAuthorizationUrl(
  com.workos.usermanagement.AuthKitAuthorizationUrlOptions(
    redirectUri = "https://example.com/callback",
    provider = "authkit",
  )
)
```

### SSO URL generation

```kotlin
// v4
val url = workos.sso
  .getAuthorizationUrl(clientId, "https://example.com/callback")
  .connection("conn_123")
  .build()

// v5
val url = workos.sso.getAuthorizationUrl(
  com.workos.sso.SSOAuthorizationUrlOptions(
    redirectUri = "https://example.com/callback",
    connection = "conn_123",
  )
)
```

## 14. Notable Additions In V5

In addition to the breaking changes above, this branch adds or expands:

- `PublicClient` for PKCE-safe public flows
- `Session` helpers for sealed AuthKit session cookies
- `Vault` helpers and client-side crypto support
- generated accessors for `Actions`, `ApiKeys`, `Connect`, `FeatureFlags`, `OrganizationDomains`, `Pipes`, and `Radar`
- broader generated model coverage, including forward-compat enum handling via `Unknown`

## Final Advice

The fastest migration path is usually:

1. upgrade Java first
2. update imports from service-local `models` / `types` packages to `com.workos.models` and `com.workos.types`
3. move `clientId` into `WorkOS(...)`
4. replace builder objects with direct method arguments
5. update list handling to `Page<T>`
6. replace webhook verification with `Webhook()`

If you do those six steps first, the remaining compile errors should mostly collapse into straightforward method renames.
