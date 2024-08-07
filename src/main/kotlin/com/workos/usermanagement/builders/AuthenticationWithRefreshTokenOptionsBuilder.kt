package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithRefreshTokenOptions

/**
 * Builder for options when authenticating with refresh token.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param refreshToken The `refresh_token` received from a successful authentication response.
 * @param organizationId The organization to issue the new `access_token` for.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithRefreshTokenOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val refreshToken: String,
  private val organizationId: String? = null,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithRefreshTokenOptions object.
   */
  fun build(): AuthenticationWithRefreshTokenOptions {
    return AuthenticationWithRefreshTokenOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "refresh_token",
      refreshToken = this.refreshToken,
      organizationId = this.organizationId,
      invitationToken = this.options?.invitationToken,
      ipAddress = this.options?.ipAddress,
      userAgent = this.options?.userAgent,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(clientId: String, clientSecret: String, refreshToken: String, organizationId: String? = null, options: AuthenticationAdditionalOptions? = null): AuthenticationWithRefreshTokenOptionsBuilder {
      return AuthenticationWithRefreshTokenOptionsBuilder(clientId, clientSecret, refreshToken, organizationId, options)
    }
  }
}
