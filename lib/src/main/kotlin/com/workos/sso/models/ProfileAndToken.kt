package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileAndToken
@JsonCreator constructor(
  @JvmField
  val profile: Profile,

  @JvmField
  @JsonProperty("access_token")
  val token: String,
)
