package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class AssignRoleOptions @JvmOverloads constructor(
  @JsonProperty("role_slug")
  val roleSlug: String,

  @JsonProperty("resource_id")
  val resourceId: String? = null,

  @JsonProperty("resource_external_id")
  val resourceExternalId: String? = null,

  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null
) {
  init {
    require(roleSlug.isNotBlank()) { "roleSlug is required" }
    require(!(resourceId != null && resourceExternalId != null)) {
      "Cannot specify both resourceId and resourceExternalId"
    }
    require(!(resourceExternalId != null && resourceTypeSlug == null)) {
      "resourceTypeSlug is required when resourceExternalId is specified"
    }
  }
}
