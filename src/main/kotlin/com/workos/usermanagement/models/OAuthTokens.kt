package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OAuth tokens from third-party providers
 *
 * @param accessToken The access token from the third-party provider.
 * @param refreshToken The refresh token from the third-party provider.
 * @param expiresAt The timestamp when the access token expires.
 * @param scopes The scopes granted to the access token.
 */
data class OAuthTokens @JsonCreator constructor(
  @JsonProperty("access_token")
  val accessToken: String,

  @JsonProperty("refresh_token")
  val refreshToken: String,

  @JsonProperty("expires_at")
  val expiresAt: Long,

  @JsonProperty("scopes")
  val scopes: List<String>,
)
