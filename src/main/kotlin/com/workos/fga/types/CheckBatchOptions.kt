package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CheckBatchOptions @JvmOverloads constructor(
  /**
   * The list of warrant checks to perform.
   */
  @JsonProperty("checks")
  val checks: List<WarrantCheckOptions>,

  @JsonProperty("debug")
  val debug: Boolean? = null,
) {
  @JsonProperty("op")
  val op: String = "batch"

  init {
    require(checks.isNotEmpty()) { "Checks is required" }
  }
}
