// @oagen-ignore-file
// Hand-maintained SSO URL builders. The corresponding OpenAPI operations
// (`GET /sso/authorize` and `GET /sso/logout`) are excluded from the Kotlin
// emitter so these functions own those names.
package com.workos.sso

import com.workos.common.http.addIfNotNull
import com.workos.common.http.buildAuthUrl
import com.workos.common.http.randomOAuthState
import com.workos.pkce.PKCE

/** Options accepted by [SSO.getAuthorizationUrl]. */
data class SSOAuthorizationUrlOptions
  @JvmOverloads
  constructor(
    /** The URI to redirect the user to after authorization. */
    val redirectUri: String,
    /** The WorkOS client ID. Falls back to the value on [com.workos.WorkOS] if omitted. */
    val clientId: String? = null,
    /** The domain of the organization to filter the connection by. */
    val domain: String? = null,
    /** The ID of the connection to initiate SSO for. */
    val connection: String? = null,
    /** The ID of the organization to initiate SSO for. */
    val organization: String? = null,
    /** The identity provider to use (e.g. "GoogleOAuth", "MicrosoftOAuth"). */
    val provider: String? = null,
    /** An opaque state value for CSRF protection, returned in the callback. */
    val state: String? = null,
    /** The PKCE code challenge derived from the code verifier. */
    val codeChallenge: String? = null,
    /** The method used to derive the code challenge (typically "S256"). */
    val codeChallengeMethod: String? = null,
    /** A hint for the identity provider domain to pre-select. */
    val domainHint: String? = null,
    /** A hint for the user's login identifier (e.g. email). */
    val loginHint: String? = null,
    /** An opaque nonce value for replay protection. */
    val nonce: String? = null,
    /** Additional OAuth scopes to request from the identity provider. */
    val providerScopes: List<String>? = null,
    /** Additional query parameters to forward to the identity provider. */
    val providerQueryParams: Map<String, String>? = null
  )

/** Result of [SSO.getAuthorizationUrlWithPKCE]. */
data class SSOPKCEAuthorizationUrlResult(
  /** The fully constructed SSO authorization URL to redirect the user to. */
  val url: String,
  /** The random state value included in the URL for CSRF protection. */
  val state: String,
  /** The PKCE code verifier to use when exchanging the authorization code. */
  val codeVerifier: String
)

/**
 * Build the SSO authorization URL. This does not make an HTTP request — it
 * returns the URL the user should be redirected to.
 *
 * At least one of [SSOAuthorizationUrlOptions.domain],
 * [SSOAuthorizationUrlOptions.connection],
 * [SSOAuthorizationUrlOptions.organization], or
 * [SSOAuthorizationUrlOptions.provider] must be supplied.
 */
fun SSO.getAuthorizationUrl(options: SSOAuthorizationUrlOptions): String {
  val resolvedClientId =
    options.clientId ?: workos.clientId
      ?: throw IllegalArgumentException("clientId is required (pass explicitly or configure it on the WorkOS client)")

  require(
    options.domain != null ||
      options.connection != null ||
      options.organization != null ||
      options.provider != null
  ) {
    "Incomplete arguments. Need to specify either 'domain', 'connection', 'organization', or 'provider'."
  }

  val params = mutableListOf<Pair<String, String>>()
  params += "redirect_uri" to options.redirectUri
  params += "client_id" to resolvedClientId
  params += "response_type" to "code"
  params.addIfNotNull("domain", options.domain)
  params.addIfNotNull("connection", options.connection)
  params.addIfNotNull("organization", options.organization)
  params.addIfNotNull("provider", options.provider)
  params.addIfNotNull("state", options.state)
  params.addIfNotNull("code_challenge", options.codeChallenge)
  params.addIfNotNull("code_challenge_method", options.codeChallengeMethod)
  params.addIfNotNull("domain_hint", options.domainHint)
  params.addIfNotNull("login_hint", options.loginHint)
  params.addIfNotNull("nonce", options.nonce)
  if (options.providerScopes != null) {
    for (scope in options.providerScopes) params += "provider_scopes" to scope
  }
  if (options.providerQueryParams != null) {
    for ((k, v) in options.providerQueryParams) params += "provider_query_params[$k]" to v
  }

  return buildAuthUrl(workos, "/sso/authorize", params)
}

/**
 * Build an SSO authorization URL with an automatically generated PKCE pair
 * and random state. The returned value carries the verifier + state the
 * caller must persist for the subsequent code exchange.
 */
fun SSO.getAuthorizationUrlWithPKCE(options: SSOAuthorizationUrlOptions): SSOPKCEAuthorizationUrlResult {
  val pair = PKCE().generate()
  val state = options.state ?: randomOAuthState()
  val url =
    getAuthorizationUrl(
      options.copy(
        codeChallenge = pair.codeChallenge,
        codeChallengeMethod = pair.codeChallengeMethod,
        state = state
      )
    )
  return SSOPKCEAuthorizationUrlResult(url = url, state = state, codeVerifier = pair.codeVerifier)
}

/** Options accepted by [SSO.getLogoutUrl]. */
data class SSOLogoutUrlOptions(
  /** The logout token obtained from [SSO.authorizeLogout]. */
  val token: String
)

/** Build the SSO logout redirect URL. Does not make an HTTP request. */
fun SSO.getLogoutUrl(options: SSOLogoutUrlOptions): String = buildAuthUrl(workos, "/sso/logout", listOf("token" to options.token))

/**
 * Exchange an SSO authorization code for a profile + token using PKCE
 * (H16). Unlike the generated `getProfileAndToken`, this variant sends the
 * [codeVerifier] and omits `client_secret` — suitable for public-client
 * flows (CLI, mobile, desktop).
 */
@JvmOverloads
fun SSO.getProfileAndTokenWithPKCE(
  code: String,
  codeVerifier: String,
  requestOptions: com.workos.common.http.RequestOptions? = null
): com.workos.models.SSOTokenResponse {
  val body = linkedMapOf<String, Any?>()
  body["grant_type"] = "authorization_code"
  body["client_id"] = workos.clientId
  body["code"] = code
  body["code_verifier"] = codeVerifier
  val config =
    com.workos.common.http.RequestConfig(
      method = "POST",
      path = "/sso/token",
      body = body,
      requestOptions = requestOptions
    )
  return workos.baseClient.request(config, com.workos.models.SSOTokenResponse::class.java)
}
