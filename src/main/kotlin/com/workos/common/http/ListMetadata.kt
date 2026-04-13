// @oagen-ignore-file
package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Pagination metadata returned alongside every list response.
 *
 *  - [before]: cursor that would reach the previous page (if any).
 *  - [after]: cursor that would reach the next page (if any).
 */
data class ListMetadata(
  @JvmField
  @JsonProperty("before")
  val before: String? = null,
  @JvmField
  @JsonProperty("after")
  val after: String? = null
)
