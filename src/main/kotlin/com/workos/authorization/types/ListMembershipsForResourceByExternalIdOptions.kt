package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListMembershipsForResourceByExternalIdOptions @JvmOverloads constructor(
  @JsonProperty("organization_id")
  val organizationId: String,

  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String,

  @JsonProperty("external_id")
  val externalId: String,

  @JsonProperty("permission_slug")
  val permissionSlug: String,

  @JsonProperty("assignment")
  val assignment: Assignment? = null,

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
    require(organizationId.isNotBlank()) { "organizationId is required" }
    require(resourceTypeSlug.isNotBlank()) { "resourceTypeSlug is required" }
    require(externalId.isNotBlank()) { "externalId is required" }
    require(permissionSlug.isNotBlank()) { "permissionSlug is required" }
  }
}
