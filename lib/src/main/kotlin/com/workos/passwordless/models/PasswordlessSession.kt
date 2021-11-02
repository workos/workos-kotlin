package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordlessSession @JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  val email: String,

  @JvmField
  @JsonProperty("expires_at")
  val expiresAt: String,

  @JvmField
  val link: String,

  @JvmField
  @JsonProperty("object")
  val obj: String? = "passwordless_session"
)
