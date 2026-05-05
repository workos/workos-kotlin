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
  /** Cursor pointing to the previous page, or `null` if this is the first page. */
  @JvmField
  @JsonProperty("before")
  val before: String? = null,
  /** Cursor pointing to the next page, or `null` if this is the last page. */
  @JvmField
  @JsonProperty("after")
  val after: String? = null
)
