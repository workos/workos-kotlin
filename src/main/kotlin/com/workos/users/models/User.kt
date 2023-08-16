package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User @JsonCreator constructor(

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("user_type")
  val userType: UserType,

  @JvmField
  @JsonProperty("email")
  val email: String,

  @JvmField
  @JsonProperty("first_name")
  val firstName: String? = null,

  @JvmField
  @JsonProperty("last_name")
  val lastName: String? = null,

  @JvmField
  @JsonProperty("email_verified_at")
  val emailVerifiedAt: String? = null,

  @JvmField
  @JsonProperty("google_oauth_profile_id")
  val googleOAuthProfileId: String? = null,

  @JvmField
  @JsonProperty("sso_profile_id")
  val ssoProfileId: String? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)


