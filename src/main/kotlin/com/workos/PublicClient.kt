// @oagen-ignore-file
// Hand-maintained public-client factory (H19). For browser, mobile, CLI,
// desktop applications that cannot securely store a WorkOS API key.
// Exposes only PKCE-safe operations (no client_secret on the wire).
package com.workos

import com.workos.common.http.RequestConfig
import com.workos.common.http.RequestOptions
import com.workos.models.AuthenticateResponse
import com.workos.pkce.PKCE
import com.workos.pkce.pkce
import com.workos.sso.SSOAuthorizationUrlOptions
import com.workos.sso.SSOPKCEAuthorizationUrlResult
import com.workos.sso.getAuthorizationUrl
import com.workos.sso.getAuthorizationUrlWithPKCE
import com.workos.sso.getProfileAndTokenWithPKCE
import com.workos.usermanagement.AuthKitAuthorizationUrlOptions
import com.workos.usermanagement.DeviceAuthenticationResponse
import com.workos.usermanagement.PKCEAuthorizationUrlResult
import com.workos.usermanagement.PollDeviceAuthorizationOptions
import com.workos.usermanagement.UserManagement
import com.workos.usermanagement.getAuthorizationUrl
import com.workos.usermanagement.getAuthorizationUrlWithPKCE
import com.workos.usermanagement.pollDeviceAuthorization
import okhttp3.OkHttpClient

/**
 * A WorkOS client locked to the PKCE / public-client surface.
 *
 * For browser, mobile, CLI, and desktop apps that cannot securely hold a
 * WorkOS API key. Only methods that are safe without `client_secret` are
 * exposed here: authorization-URL builders, PKCE code exchange, device-flow
 * polling, JWKS, and the PKCE helper.
 *
 * Construct via [PublicClient.create]. The underlying [WorkOS] instance is
 * configured with an empty API key so any accidental calls into the full
 * service API surface are rejected by the server.
 */
class PublicClient private constructor(
  internal val workos: WorkOS
) {
  val clientId: String get() = workos.clientId!!

  val pkce: PKCE get() = workos.pkce

  /** Build an AuthKit authorization URL. */
  fun getAuthorizationUrl(options: AuthKitAuthorizationUrlOptions): String = UserManagement(workos).getAuthorizationUrl(options)

  /** Build an AuthKit authorization URL with auto-generated PKCE params and state. */
  fun getAuthorizationUrlWithPKCE(options: AuthKitAuthorizationUrlOptions): PKCEAuthorizationUrlResult =
    UserManagement(workos).getAuthorizationUrlWithPKCE(options)

  /** Build an SSO authorization URL. */
  fun getSSOAuthorizationUrl(options: SSOAuthorizationUrlOptions): String =
    com.workos.sso
      .SSO(workos)
      .getAuthorizationUrl(options)

  /** Build an SSO authorization URL with auto-generated PKCE params and state. */
  fun getSSOAuthorizationUrlWithPKCE(options: SSOAuthorizationUrlOptions): SSOPKCEAuthorizationUrlResult =
    com.workos.sso
      .SSO(workos)
      .getAuthorizationUrlWithPKCE(options)

  /**
   * Exchange an AuthKit authorization code + PKCE code verifier for tokens.
   * No `client_secret` is sent — this is the PKCE/public-client flow.
   */
  @JvmOverloads
  fun authenticateWithCode(
    code: String,
    codeVerifier: String,
    ipAddress: String? = null,
    deviceId: String? = null,
    userAgent: String? = null,
    requestOptions: RequestOptions? = null
  ): AuthenticateResponse {
    val body = linkedMapOf<String, Any?>()
    body["code"] = code
    body["code_verifier"] = codeVerifier
    if (ipAddress != null) body["ip_address"] = ipAddress
    if (deviceId != null) body["device_id"] = deviceId
    if (userAgent != null) body["user_agent"] = userAgent
    body["grant_type"] = "authorization_code"
    body["client_id"] = workos.clientId
    val config =
      RequestConfig(
        method = "POST",
        path = "/user_management/authenticate",
        body = body,
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, AuthenticateResponse::class.java)
  }

  /**
   * Exchange an SSO authorization code + PKCE verifier for a profile and
   * token, without sending `client_secret`.
   */
  @JvmOverloads
  fun getSSOProfileAndTokenWithPKCE(
    code: String,
    codeVerifier: String,
    requestOptions: RequestOptions? = null
  ) = com.workos.sso
    .SSO(workos)
    .getProfileAndTokenWithPKCE(code, codeVerifier, requestOptions)

  /** Begin a device-authorization grant (returns the user code + verification URIs). */
  @JvmOverloads
  fun createDevice(requestOptions: RequestOptions? = null) =
    UserManagement(workos).createDevice(clientId = clientId, requestOptions = requestOptions)

  /** Poll the token-exchange endpoint until the device is authorized. */
  fun pollDeviceAuthorization(options: PollDeviceAuthorizationOptions): DeviceAuthenticationResponse =
    UserManagement(workos).pollDeviceAuthorization(options)

  /** Fetch the JWKS for this client (used for access-token verification). */
  @JvmOverloads
  fun getJwks(requestOptions: RequestOptions? = null) = UserManagement(workos).getJwks(clientId = clientId, requestOptions = requestOptions)

  companion object {
    /**
     * Create a PKCE-only WorkOS client.
     *
     * @param clientId the WorkOS client ID. Required.
     * @param apiBaseUrl override the base URL; defaults to `https://api.workos.com`.
     * @param httpClient provide a custom OkHttp client (timeouts, interceptors).
     */
    @JvmStatic
    @JvmOverloads
    fun create(
      clientId: String,
      apiBaseUrl: String = WorkOS.DEFAULT_BASE_URL,
      httpClient: OkHttpClient? = null
    ): PublicClient {
      require(clientId.isNotEmpty()) { "clientId is required for a public client" }
      val workos =
        if (httpClient == null) {
          WorkOS(apiKey = "", clientId = clientId, apiBaseUrl = apiBaseUrl)
        } else {
          WorkOS(apiKey = "", clientId = clientId, apiBaseUrl = apiBaseUrl, httpClient = httpClient)
        }
      return PublicClient(workos)
    }
  }
}
