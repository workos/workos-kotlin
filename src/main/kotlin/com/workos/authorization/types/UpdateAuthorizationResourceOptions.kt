package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class UpdateAuthorizationResourceOptions @JvmOverloads constructor(
  @JsonProperty("name")
  val name: String? = null,

  @JsonProperty("description")
  val description: String? = null,

  @JsonProperty("parent_resource_id")
  val parentResourceId: String? = null,

  @JsonProperty("parent_resource_external_id")
  val parentResourceExternalId: String? = null,

  @JsonProperty("parent_resource_type_slug")
  val parentResourceTypeSlug: String? = null
)
