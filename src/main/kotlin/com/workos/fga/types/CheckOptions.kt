package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CheckOptions @JvmOverloads constructor(
  /**
   * The operator to use when checks contains a list of more than one warrant check.
   */
  @JsonProperty("op")
  val op: String? = null,

  /**
   * The list of warrant checks to perform.
   */
  @JsonProperty("checks")
  val checks: List<WarrantCheckOptions>,

  @JsonProperty("debug")
  val debug: Boolean? = null,
) {
  init {
    require(checks.isNotEmpty()) { "Checks is required" }

    if (checks.size > 1) {
      require(!op.isNullOrBlank()) { "Op is required when more than one check is passed" }
    }
  }
}
