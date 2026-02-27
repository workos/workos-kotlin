package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListResourcesForMembershipOptions @JvmOverloads constructor(
  @JsonProperty("organization_membership_id")
  val organizationMembershipId: String,

  @JsonProperty("permission_slug")
  val permissionSlug: String,

  @JsonProperty("parent_resource_id")
  val parentResourceId: String? = null,

  @JsonProperty("parent_resource_type_slug")
  val parentResourceTypeSlug: String? = null,

  @JsonProperty("parent_resource_external_id")
  val parentResourceExternalId: String? = null,

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
    require(permissionSlug.isNotBlank()) { "permissionSlug is required" }
    require(!(parentResourceId != null && parentResourceExternalId != null)) {
      "Cannot specify both parentResourceId and parentResourceExternalId"
    }
  }
}
