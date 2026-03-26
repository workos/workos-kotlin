package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuthenticationEventStatusEnumType(val type: String) {
  /**
   * Failed
   */
  @JsonProperty("failed")
  Failed("failed"),

  /**
   * Started
   */
  @JsonProperty("started")
  Started("started"),

  /**
   * Succeeded
   */
  @JsonProperty("succeeded")
  Succeeded("succeeded"),

  /**
   * Timed Out
   */
  @JsonProperty("timed_out")
  TimedOut("timed_out"),
}
