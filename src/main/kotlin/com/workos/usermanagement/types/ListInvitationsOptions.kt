package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListInvitationsOptions @JvmOverloads constructor(
  @JsonProperty("email")
  val email: String? = null,

  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("order")
  val order: Order? = null,

  @JsonProperty("before")
  val before: String? = null,

  @JsonProperty("after")
  val after: String? = null
)
