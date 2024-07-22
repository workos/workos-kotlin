package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuthenticationEventStatusEnumType(val type: String) {
  /**
   * Failed
   */
  @JsonProperty("failed")
  Failed("failed"),

  /**
   * Succeeded
   */
  @JsonProperty("succeeded")
  Succeeded("succeeded"),
}
