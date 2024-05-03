package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class CreateMagicAuthOptions(
  /**
   * The email address of the user.
   */
  @JsonProperty("email")
  val email: String,

  /**
   * The token of an invitation, if required.
   */
  @JsonProperty("invitation_token")
  val invitationToken: String? = null,
) {
  init {
    require(email.isNotBlank()) { "Email is required" }
  }
}
