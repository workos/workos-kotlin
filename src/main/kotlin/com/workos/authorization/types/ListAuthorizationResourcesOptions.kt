package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListAuthorizationResourcesOptions @JvmOverloads constructor(
  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null,

  @JsonProperty("parent_resource_id")
  val parentResourceId: String? = null,

  @JsonProperty("parent_resource_type_slug")
  val parentResourceTypeSlug: String? = null,

  @JsonProperty("parent_external_id")
  val parentExternalId: String? = null,

  @JsonProperty("search")
  val search: String? = null,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("order")
  val order: Order? = null,

  @JsonProperty("before")
  val before: String? = null,

  @JsonProperty("after")
  val after: String? = null
)
