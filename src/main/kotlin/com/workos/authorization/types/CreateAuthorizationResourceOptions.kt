package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateAuthorizationResourceOptions @JvmOverloads constructor(
  @JsonProperty("external_id")
  val externalId: String,

  @JsonProperty("name")
  val name: String,

  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String,

  @JsonProperty("organization_id")
  val organizationId: String,

  @JsonProperty("description")
  val description: String? = null,

  @JsonProperty("parent_resource_id")
  val parentResourceId: String? = null,

  @JsonProperty("parent_resource_external_id")
  val parentResourceExternalId: String? = null,

  @JsonProperty("parent_resource_type_slug")
  val parentResourceTypeSlug: String? = null
)
