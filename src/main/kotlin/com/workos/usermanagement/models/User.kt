package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A WorkOS user
 *
 * @param id The unique ID of the user.
 * @param email The email address of the user.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param emailVerified Whether the user’s email has been verified.
 * @param profilePictureUrl A URL reference to an image representing the user.
 * @param lastSignInAt The timestamp when the user last signed in.
 * @param createdAt The timestamp when the user was created.
 * @param updatedAt The timestamp when the user was last updated.
 */
data class User @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("first_name")
  val firstName: String? = null,

  @JsonProperty("last_name")
  val lastName: String? = null,

  @JsonProperty("email_verified")
  val emailVerified: Boolean,

  @JsonProperty("profile_picture_url")
  val profilePictureUrl: String? = null,

  @JsonProperty("last_sign_in_at")
  val lastSignInAt: String? = null,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String
)
