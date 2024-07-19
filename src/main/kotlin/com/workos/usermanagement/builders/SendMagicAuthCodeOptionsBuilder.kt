package com.workos.usermanagement.builders

import com.workos.usermanagement.types.SendMagicAuthCodeOptions

/**
 * Builder for options when sending a magic auth code.
 *
 * @param email The email address of the user.
 */
class SendMagicAuthCodeOptionsBuilder @JvmOverloads constructor(
  private var email: String,
) {
  /**
   * Generates the SendMagicAuthCodeOptions object.
   */
  fun build(): SendMagicAuthCodeOptions {
    return SendMagicAuthCodeOptions(
      email = this.email,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String): SendMagicAuthCodeOptionsBuilder {
      return SendMagicAuthCodeOptionsBuilder(email)
    }
  }
}
