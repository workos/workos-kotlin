package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class OrganizationMembershipStatusEnumType(val type: String) {
  /**
   * Active
   */
  @JsonProperty("active")
  Active("active"),

  /**
   * Pending
   */
  @JsonProperty("pending")
  Pending("pending"),
}
