// @oagen-ignore-file
package com.workos.common.exceptions

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Structured error detail returned in WorkOS API error responses.
 *
 * The shape comes from the API's `errors[]` array — typically one entry per validation
 * failure. Use `field` to identify which input was invalid and `message` for a
 * human-readable explanation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiError
  @JvmOverloads
  constructor(
    /** Name of the input field that caused the error, when applicable. */
    @JvmField val field: String? = null,
    /** Machine-readable error code identifying the failure. */
    @JvmField val code: String? = null,
    /** Human-readable explanation of the error. */
    @JvmField val message: String? = null
  )
