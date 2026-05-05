// @oagen-ignore-file
// Hand-maintained AuthKit authorization URL builders. The corresponding
// OpenAPI operations (`GET /user_management/authorize` and
// `GET /user_management/sessions/logout`) are excluded from the Kotlin
// emitter so these functions own those names.
package com.workos.usermanagement

import com.workos.common.http.addIfNotNull
import com.workos.common.http.buildAuthUrl
import com.workos.common.http.randomOAuthState
import com.workos.pkce.PKCE

/** Options accepted by [UserManagement.getAuthorizationUrl]. */
data class AuthKitAuthorizationUrlOptions
  @JvmOverloads
  constructor(
    /** The URI to redirect the user to after authorization. */
    val redirectUri: String,
    /** The WorkOS client ID. Falls back to the value on [com.workos.WorkOS] if omitted. */
    val clientId: String? = null,
    /** The authentication provider (e.g. "authkit", "GoogleOAuth"). */
    val provider: String? = null,
    /** The ID of the connection to authenticate through. */
    val connectionId: String? = null,
    /** The ID of the organization to authenticate into. */
    val organizationId: String? = null,
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
    /** Hint for the AuthKit screen to show ("sign-up" or "sign-in"). Only valid when provider is "authkit". */
    val screenHint: String? = null,
    /** The prompt parameter to pass to the authorization endpoint. */
    val prompt: String? = null,
    /** Additional OAuth scopes to request from the identity provider. */
    val providerScopes: List<String>? = null,
    /** Additional query parameters to forward to the identity provider. */
    val providerQueryParams: Map<String, String>? = null,
    /** A token from an invitation, used to link the new user to an organization. */
    val invitationToken: String? = null
  )

/** Result of [UserManagement.getAuthorizationUrlWithPKCE]. */
data class PKCEAuthorizationUrlResult(
  /** The fully constructed authorization URL to redirect the user to. */
  val url: String,
  /** The random state value included in the URL for CSRF protection. */
  val state: String,
  /** The PKCE code verifier to use when exchanging the authorization code. */
  val codeVerifier: String
)

/**
 * Build an AuthKit authorization URL. This does not make an HTTP request — it
 * constructs the URL the user should be redirected to.
 *
 * At least one of [AuthKitAuthorizationUrlOptions.provider],
 * [AuthKitAuthorizationUrlOptions.connectionId], or
 * [AuthKitAuthorizationUrlOptions.organizationId] must be provided.
 */
fun UserManagement.getAuthorizationUrl(options: AuthKitAuthorizationUrlOptions): String {
  val resolvedClientId =
    options.clientId ?: workos.clientId
      ?: throw IllegalArgumentException("clientId is required (pass explicitly or configure it on the WorkOS client)")

  require(options.provider != null || options.connectionId != null || options.organizationId != null) {
    "Incomplete arguments. Need to specify either 'connectionId', 'organizationId', or 'provider'."
  }
  require(options.provider == "authkit" || options.screenHint == null) {
    "'screenHint' is only supported for the 'authkit' provider."
  }

  val params = mutableListOf<Pair<String, String>>()
  params += "redirect_uri" to options.redirectUri
  params += "client_id" to resolvedClientId
  params += "response_type" to "code"
  params.addIfNotNull("provider", options.provider)
  params.addIfNotNull("connection_id", options.connectionId)
  params.addIfNotNull("organization_id", options.organizationId)
  params.addIfNotNull("state", options.state)
  params.addIfNotNull("code_challenge", options.codeChallenge)
  params.addIfNotNull("code_challenge_method", options.codeChallengeMethod)
  params.addIfNotNull("domain_hint", options.domainHint)
  params.addIfNotNull("login_hint", options.loginHint)
  params.addIfNotNull("screen_hint", options.screenHint)
  params.addIfNotNull("prompt", options.prompt)
  params.addIfNotNull("invitation_token", options.invitationToken)
  if (options.providerScopes != null) {
    for (scope in options.providerScopes) params += "provider_scopes" to scope
  }
  if (options.providerQueryParams != null) {
    for ((k, v) in options.providerQueryParams) params += "provider_query_params[$k]" to v
  }

  return buildAuthUrl(workos, "/user_management/authorize", params)
}

/**
 * Build an AuthKit authorization URL with an automatically generated PKCE
 * pair and random state. The returned [PKCEAuthorizationUrlResult] carries
 * the code verifier and state that the caller must persist until the
 * subsequent token exchange.
 */
fun UserManagement.getAuthorizationUrlWithPKCE(options: AuthKitAuthorizationUrlOptions): PKCEAuthorizationUrlResult {
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
  return PKCEAuthorizationUrlResult(url = url, state = state, codeVerifier = pair.codeVerifier)
}

/** Options accepted by [UserManagement.getLogoutUrl]. */
data class AuthKitLogoutUrlOptions
  @JvmOverloads
  constructor(
    /** The ID of the session to terminate. */
    val sessionId: String,
    /** The URL to redirect the user to after logout. */
    val returnTo: String? = null
  )

/** Build the AuthKit logout URL. Does not make an HTTP request. */
fun UserManagement.getLogoutUrl(options: AuthKitLogoutUrlOptions): String {
  val params = mutableListOf<Pair<String, String>>()
  params += "session_id" to options.sessionId
  params.addIfNotNull("return_to", options.returnTo)
  return buildAuthUrl(workos, "/user_management/sessions/logout", params)
}
