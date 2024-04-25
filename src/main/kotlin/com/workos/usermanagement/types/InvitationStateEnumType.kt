package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class InvitationStateEnumType(val type: String) {
  /**
   * Accepted
   */
  @JsonProperty("accepted")
  Accepted("accepted"),

  /**
   * Pending
   */
  @JsonProperty("pending")
  Pending("pending"),

  /**
   * Expired
   */
  @JsonProperty("expired")
  Expired("expired"),

  /**
   * Revoked
   */
  @JsonProperty("revoked")
  Revoked("revoked"),
}
