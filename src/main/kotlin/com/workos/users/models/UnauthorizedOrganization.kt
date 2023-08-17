package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UnauthorizedOrganization @JsonCreator constructor(

  @JvmField
  @JsonProperty("organization")
  val organization: Organization,

  @JvmField
  @JsonProperty("reasons")
  val reasons: List<UnauthorizedOrganizationReason>,
)
