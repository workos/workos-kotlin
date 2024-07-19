package com.workos.usermanagement.builders

import com.workos.usermanagement.types.SendPasswordResetEmailOptions

/**
 * Builder for options when sending a password reset email.
 *
 * @param email The email address of the user.
 * @param passwordResetUrl The URL to include in the email.
 */
class SendPasswordResetEmailOptionsBuilder(
  private var email: String,
  private var passwordResetUrl: String,
) {
  /**
   * Generates the SendPasswordResetEmailOptions object.
   */
  fun build(): SendPasswordResetEmailOptions {
    return SendPasswordResetEmailOptions(
      email = this.email,
      passwordResetUrl = this.passwordResetUrl
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String, passwordResetUrl: String): SendPasswordResetEmailOptionsBuilder {
      return SendPasswordResetEmailOptionsBuilder(email, passwordResetUrl)
    }
  }
}
