package com.workos.usermanagement.builders

import com.workos.usermanagement.types.ResetPasswordOptions

/**
 * Builder for options when resetting a password.
 *
 * @param token The token query parameter from the password reset URL.
 * @param newPassword The new password to set for the user.
 */
class ResetPasswordOptionsBuilder(
  private var token: String,
  private var newPassword: String,
) {
  /**
   * Generates the ResetPasswordOptions object.
   */
  fun build(): ResetPasswordOptions {
    return ResetPasswordOptions(
      token = this.token,
      newPassword = this.newPassword
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(token: String, newPassword: String): ResetPasswordOptionsBuilder {
      return ResetPasswordOptionsBuilder(token, newPassword)
    }
  }
}
