package com.workos.usermanagement.builders

import com.workos.usermanagement.types.EnrolledAuthenticationFactorOptions

/**
 * Builder for options when enrolling an MFA factor.
 *
 * @param totpIssuer Your application or company name displayed in the user’s authenticator app.
 * @param totpUser The user’s account name displayed in their authenticator app.
 */
class EnrolledAuthenticationFactorOptionsBuilder @JvmOverloads constructor(
  private var totpIssuer: String? = null,
  private var totpUser: String? = null,
) {
  /**
   * TOTP Issuer
   */
  fun totpIssuer(value: String) = apply { totpIssuer = value }

  /**
   * TOTP User
   */
  fun totpUser(value: String) = apply { totpUser = value }

  /**
   * Generates the EnrolledAuthenticationFactorOptions object.
   */
  fun build(): EnrolledAuthenticationFactorOptions {
    return EnrolledAuthenticationFactorOptions(
      type = "totp",
      totpIssuer = this.totpIssuer,
      totpUser = this.totpUser,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(): EnrolledAuthenticationFactorOptionsBuilder {
      return EnrolledAuthenticationFactorOptionsBuilder()
    }
  }
}
