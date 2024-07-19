package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithEmailVerificationOptions

/**
 * Builder for options when authenticating with email verification.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param code The one-time code that was emailed to the user.
 * @param pendingAuthenticationToken The authentication token returned from a failed authentication attempt due to the corresponding error.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithEmailVerificationOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val code: String,
  private val pendingAuthenticationToken: String,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithEmailVerificationOptions object.
   */
  fun build(): AuthenticationWithEmailVerificationOptions {
    return AuthenticationWithEmailVerificationOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "urn:workos:oauth:grant-type:email-verification:code",
      code = this.code,
      pendingAuthenticationToken = this.pendingAuthenticationToken,
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
    fun create(clientId: String, clientSecret: String, code: String, pendingAuthenticationToken: String, options: AuthenticationAdditionalOptions? = null): AuthenticationWithEmailVerificationOptionsBuilder {
      return AuthenticationWithEmailVerificationOptionsBuilder(clientId, clientSecret, code, pendingAuthenticationToken, options)
    }
  }
}
