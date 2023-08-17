package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizedOrganization @JsonCreator constructor(

  @JvmField
  @JsonProperty("organization")
  val organization: Organization,
)
