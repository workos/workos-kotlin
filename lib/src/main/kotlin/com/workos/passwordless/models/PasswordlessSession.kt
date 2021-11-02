package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordlessSession @JsonCreator constructor(
  val id: String,

  val email: String,

  @JsonProperty("expires_at")
  val expiresAt: String,

  val link: String,

  @JsonProperty("object")
  val obj: String? = "passwordless_session"
)
