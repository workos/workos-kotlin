package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListRoleAssignmentsOptions @JvmOverloads constructor(
  @JsonProperty("organization_membership_id")
  val organizationMembershipId: String,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("order")
  val order: Order? = null,

  @JsonProperty("before")
  val before: String? = null,

  @JsonProperty("after")
  val after: String? = null
) {
  init {
    require(organizationMembershipId.isNotBlank()) { "organizationMembershipId is required" }
  }
}
