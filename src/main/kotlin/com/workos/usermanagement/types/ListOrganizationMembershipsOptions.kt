package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListOrganizationMembershipsOptions @JvmOverloads constructor(
  @JsonProperty("user_id")
  val userId: String? = null,

  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JsonProperty("statuses")
  val statuses: List<OrganizationMembershipStatusEnumType>? = null,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("order")
  val order: Order? = null,

  @JsonProperty("before")
  val before: String? = null,

  @JsonProperty("after")
  val after: String? = null
)
