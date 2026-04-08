package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class RemoveRoleOptions @JvmOverloads constructor(
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
    require(roleSlug.isNotBlank()) { "Role slug is required" }
    require(resourceId != null || (resourceExternalId != null && resourceTypeSlug != null) || (resourceId == null && resourceExternalId == null)) {
      "Specify either resourceId or both resourceExternalId and resourceTypeSlug"
    }
  }
}
