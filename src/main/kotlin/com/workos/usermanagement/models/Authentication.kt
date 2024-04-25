package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication object
 *
 * @param user The corresponding [User] object.
 * @param organizationId The organization the user selected to sign in to.
 * @param accessToken A JWT containing information about the session.
 * @param refreshToken Exchange this token for a new access token.
 * @param impersonator An impersonation definition.
 */
data class Authentication @JsonCreator constructor(
  @JsonProperty("user")
  val user: User? = null,

  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JsonProperty("access_token")
  val accessToken: String? = null,

  @JsonProperty("refresh_token")
  val refreshToken: String? = null,

  @JsonProperty("impersonator")
  val impersonator: AuthenticationImpersonator? = null,
)
