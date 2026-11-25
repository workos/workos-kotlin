# WorkOS workload identity for AWS

Lets the [WorkOS Kotlin SDK](../README.md#workload-identity) authenticate from AWS without a stored API key. The provider calls STS `GetWebIdentityToken` with the workload's own AWS credentials, and the SDK exchanges the token it returns for a WorkOS access token.

## Installation

Replace `VERSION` with the latest release of each artifact on Maven Central. This artifact is versioned separately from the SDK.

```kotlin
dependencies {
  implementation("com.workos:workos:VERSION")
  implementation("com.workos:workos-workload-identity-aws:VERSION")
}
```

## Usage

```kotlin
import com.workos.workloadidentity.WorkloadIdentity
import com.workos.workloadidentity.WorkloadIdentityCredentials
import com.workos.workloadidentity.aws.awsSts

val credentials = WorkloadIdentityCredentials(
  identityTokenProvider = awsSts("https://api.workos.com", region = "us-east-1"),
  clientId = "client_123",
  tokenUrl = "https://auth.example.com/oauth2/token",
  environmentId = "environment_123",
)
val workos = WorkloadIdentity.client(credentials)
```

The role needs permission for `sts:GetWebIdentityToken`, and the account needs outbound web identity federation enabled. The region is optional when the AWS SDK can find one. This is the only WorkOS artifact that depends on the AWS SDK. From Java, the factory is `AwsSts.awsSts(audience, region)`.

Each call to the provider makes one STS request, with 10 seconds to connect and 10 seconds for the whole request. The AWS SDK's own retries are switched off for the provider's STS client, because the WorkOS SDK calls the provider again when an exchange attempt fails.

The provider owns its STS client and the connection pool that comes with it. A provider that lives as long as the application needs no care. Call `close()` on a provider that you replace or stop using.

See [Workload identity](../README.md#workload-identity) in the SDK's README for every setting and for error handling.
