package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithMagicAuthOptions

/**
 * Builder for options when authenticating with Magic Auth.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param email The email address of the user.
 * @param code The one-time code that was emailed to the user.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithMagicAuthOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val email: String,
  private val code: String,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithMagicAuthOptions object.
   */
  fun build(): AuthenticationWithMagicAuthOptions {
    return AuthenticationWithMagicAuthOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "urn:workos:oauth:grant-type:magic-auth:code",
      email = this.email,
      code = this.code,
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
    fun create(clientId: String, clientSecret: String, email: String, code: String, options: AuthenticationAdditionalOptions? = null): AuthenticationWithMagicAuthOptionsBuilder {
      return AuthenticationWithMagicAuthOptionsBuilder(clientId, clientSecret, email, code, options)
    }
  }
}
