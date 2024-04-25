package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A refresh authentication object
 *
 * @param accessToken A JWT containing information about the session.
 * @param refreshToken Exchange this token for a new access token.
 */
data class RefreshAuthentication @JsonCreator constructor(
  @JsonProperty("access_token")
  val accessToken: String,

  @JsonProperty("refresh_token")
  val refreshToken: String,
)
