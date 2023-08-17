package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UnauthorizedOrganizationReason @JsonCreator constructor(

  @JvmField
  @JsonProperty("type")
  val type: String,

  @JvmField
  @JsonProperty("allowedAuthenticationMethods")
  val allowedAuthenticationMethods: SessionAuthenticationMethod
)
