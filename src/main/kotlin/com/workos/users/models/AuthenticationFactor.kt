package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthenticationFactor @JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val objectName: String,

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  @JsonProperty("type")
  val type: String,

  @JvmField
  @JsonProperty("totp")
  val totp: TOTP? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
