package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions

/**
 * Builder for options when authenticating with an authorization code.
 *
 * @param invitationToken The token of an invitation. The invitation should be in the pending state.
 * @param ipAddress The IP address of the request from the user who is attempting to authenticate.
 * @param userAgent The user agent of the request from the user who is attempting to authenticate.
 */
class AuthenticationAdditionalOptionsBuilder(
  private var invitationToken: String? = null,
  private var ipAddress: String? = null,
  private var userAgent: String? = null
) {
  /**
   * Invitation Code
   */
  fun invitationToken(value: String) = apply { invitationToken = value }

  /**
   * IP Address
   */
  fun ipAddress(value: String) = apply { ipAddress = value }

  /**
   * User Agent
   */
  fun userAgent(value: String) = apply { userAgent = value }

  /**
   * Generates the AuthenticationAdditionalOptions object.
   */
  fun build(): AuthenticationAdditionalOptions {
    return AuthenticationAdditionalOptions(
      invitationToken = this.invitationToken,
      ipAddress = this.ipAddress,
      userAgent = this.userAgent,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(): AuthenticationAdditionalOptionsBuilder {
      return AuthenticationAdditionalOptionsBuilder()
    }
  }
}
