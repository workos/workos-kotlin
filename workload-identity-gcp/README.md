# WorkOS workload identity for Google Cloud

Lets the [WorkOS Kotlin SDK](../README.md#workload-identity) authenticate from Google Cloud without a stored API key. The provider asks the metadata server for an identity token for the workload's default service account, which the SDK exchanges for a WorkOS access token.

## Installation

Replace `VERSION` with the latest release of each artifact on Maven Central. This artifact is versioned separately from the SDK.

```kotlin
dependencies {
  implementation("com.workos:workos:VERSION")
  implementation("com.workos:workos-workload-identity-gcp:VERSION")
}
```

## Usage

```kotlin
import com.workos.workloadidentity.WorkloadIdentity
import com.workos.workloadidentity.WorkloadIdentityCredentials
import com.workos.workloadidentity.gcp.gcpMetadata

val credentials = WorkloadIdentityCredentials(
  identityTokenProvider = gcpMetadata("https://api.workos.com"),
  clientId = "client_123",
  tokenUrl = "https://auth.example.com/oauth2/token",
  environmentId = "environment_123",
)
val workos = WorkloadIdentity.client(credentials)
```

It works wherever the metadata server is reachable: Compute Engine, GKE, Cloud Run, Cloud Functions and App Engine. From Java, the factory is `GcpMetadata.gcpMetadata(audience)`.

See [Workload identity](../README.md#workload-identity) in the SDK's README for every setting and for error handling.
