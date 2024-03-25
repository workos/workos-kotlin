package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User @JsonCreator constructor(

  @JvmField
  @JsonProperty("id")
  val id: String,

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
  @JsonProperty("email_verified")
  val emailVerifiedAt: Boolean,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
