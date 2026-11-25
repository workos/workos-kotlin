// @oagen-ignore-file
// Hand-maintained. `WorkOS.kt` is generated, so this accessor is an extension
// function in a file of its own instead of a member of the class.
package com.workos

import com.workos.workloadidentity.CredentialsInterceptor

/**
 * The API key of this client, for a request that sends it as `client_secret`:
 * the sign-in calls on `/user_management/authenticate`, the SSO token
 * exchange on `/sso/token`, and session refresh. Code that fills
 * `client_secret` calls this instead of reading [WorkOS.apiKey], so that a
 * client that can never make the request is refused in one place.
 *
 * A client built with an API key gets the key. A client with no API key and
 * no credentials, such as a [PublicClient], gets the empty key it was built
 * with, as before. A client built by
 * [com.workos.workloadidentity.WorkloadIdentity.client] has no API key and
 * can never make such a request, so the call fails here, before a request is
 * sent and before an access token is fetched.
 *
 * Hidden from Java.
 *
 * @param method the name of the calling method, for the error message.
 * @throws IllegalStateException when the client authenticates with workload
 *   identity credentials and has no API key.
 */
@JvmSynthetic
internal fun WorkOS.clientSecret(method: String): String? {
  if (apiKey.isNotBlank()) return apiKey
  check(httpClient.interceptors.none { it is CredentialsInterceptor }) {
    "$method needs an API key: this request sends it as the client secret. " +
      "A client that authenticates with workload identity has no API key, " +
      "so use a client built with one for this call."
  }
  return apiKey
}
