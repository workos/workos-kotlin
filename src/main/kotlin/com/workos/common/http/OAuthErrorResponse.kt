package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Error response format for OAuth/Authentication endpoints.
 * These endpoints return errors in the OAuth 2.0 standard format.
 */
internal data class OAuthErrorResponse
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JsonProperty("error")
  val error: String? = null,

  @JsonProperty("error_description")
  val errorDescription: String? = null
)
