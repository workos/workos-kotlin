package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithCodeOptions

/**
 * Builder for options when authenticating with an authorization code.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param code The authorization value which was passed back as a query parameter in the callback to the redirect URI.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithCodeOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val code: String,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithCodeOptions object.
   */
  fun build(): AuthenticationWithCodeOptions {
    return AuthenticationWithCodeOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "authorization_code",
      code = this.code,
      invitationCode = this.options?.invitationCode,
      ipAddress = this.options?.ipAddress,
      userAgent = this.options?.userAgent,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(clientId: String, clientSecret: String, code: String, options: AuthenticationAdditionalOptions? = null): AuthenticationWithCodeOptionsBuilder {
      return AuthenticationWithCodeOptionsBuilder(clientId, clientSecret, code, options)
    }
  }
}
