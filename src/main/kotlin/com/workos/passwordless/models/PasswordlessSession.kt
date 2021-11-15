package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkOS Passwordless Session. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id The unique identifier of the passwordless session.
 * @param email The email address of the user for the session.
 * @param expiresAt The ISO-8601 datetime at which the session expires.
 * @param link The link for the user to authenticate with. You can use this link to send a custom email to the user, or send an email using Email a Magic Link to the user. Once a user has authenticated with the link, WorkOS issues a redirect to the Environment's default redirect URI, with a `code` parameter and, if provided during session creation, a `state` parameter. `code` can then be exchanged for an access token and user Profile. To perform this exchange, the Developer should make a `POST` request to the `/sso/token` endpoint.
 */
data class PasswordlessSession @JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String? = "passwordless_session",

  @JvmField
  val id: String,

  @JvmField
  val email: String,

  @JvmField
  @JsonProperty("expires_at")
  val expiresAt: String,

  @JvmField
  val link: String,
)
