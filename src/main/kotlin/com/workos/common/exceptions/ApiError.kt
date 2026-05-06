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
    @JvmField val field: String? = null,
    @JvmField val code: String? = null,
    @JvmField val message: String? = null
  )
