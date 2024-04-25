package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithPasswordOptions

/**
 * Builder for options when authenticating with email and password.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param email The email of the user.
 * @param password The password of the user.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithPasswordOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val email: String,
  private val password: String,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithPasswordOptions object.
   */
  fun build(): AuthenticationWithPasswordOptions {
    return AuthenticationWithPasswordOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "password",
      email = this.email,
      password = this.password,
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
    fun create(clientId: String, clientSecret: String, email: String, password: String, options: AuthenticationAdditionalOptions? = null): AuthenticationWithPasswordOptionsBuilder {
      return AuthenticationWithPasswordOptionsBuilder(clientId, clientSecret, email, password, options)
    }
  }
}
