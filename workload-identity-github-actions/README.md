# WorkOS workload identity for GitHub Actions

Lets the [WorkOS Kotlin SDK](../README.md#workload-identity) authenticate from a GitHub Actions workflow without a stored API key. The provider asks the workflow run's OIDC endpoint for an identity token, which the SDK exchanges for a WorkOS access token.

## Installation

Replace `VERSION` with the latest release of each artifact on Maven Central. This artifact is versioned separately from the SDK.

```kotlin
dependencies {
  implementation("com.workos:workos:VERSION")
  implementation("com.workos:workos-workload-identity-github-actions:VERSION")
}
```

## Usage

```kotlin
import com.workos.workloadidentity.WorkloadIdentity
import com.workos.workloadidentity.WorkloadIdentityCredentials
import com.workos.workloadidentity.githubactions.githubActions

val credentials = WorkloadIdentityCredentials(
  identityTokenProvider = githubActions("https://api.workos.com"),
  clientId = "client_123",
  tokenUrl = "https://auth.example.com/oauth2/token",
  environmentId = "environment_123",
)
val workos = WorkloadIdentity.client(credentials)
```

The workflow or job needs `permissions: id-token: write`. From Java, the factory is `GitHubActions.githubActions(audience)`.

See [Workload identity](../README.md#workload-identity) in the SDK's README for every setting and for error handling.
