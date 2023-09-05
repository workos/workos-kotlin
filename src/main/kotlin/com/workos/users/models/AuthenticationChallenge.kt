package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthenticationChallenge @JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val objectName: String,

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("authentication_factor_id")
  val authenticationFactorId: String,

  @JvmField
  @JsonProperty("expires_at")
  val type: String,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
