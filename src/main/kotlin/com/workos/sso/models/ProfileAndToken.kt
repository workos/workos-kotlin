package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Profile] and an access token that can be used to
 * manage sessions. This class is not meant to be instantiated directly.
 *
 * @param profile [Profile]
 * @param token An access token that can be used to manage sessions like one would a normal OAuth access token. Access tokens are one-time use and expire 10 minutes after they’re created. Session duration is up to the Developer.
 */
data class ProfileAndToken
@JsonCreator constructor(
  @JvmField
  val profile: Profile,

  @JvmField
  @JsonProperty("access_token")
  val token: String,
)
