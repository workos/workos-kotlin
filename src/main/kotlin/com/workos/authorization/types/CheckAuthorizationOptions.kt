package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CheckAuthorizationOptions @JvmOverloads constructor(
  @JsonProperty("organization_membership_id")
  val organizationMembershipId: String,
  
  @JsonProperty("permission_slug")
  val permissionSlug: String,

  @JsonProperty("resource_id")
  val resourceId: String? = null,

  @JsonProperty("resource_external_id")
  val resourceExternalId: String? = null,

  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null
) {
  init {
    require(permissionSlug.isNotBlank()) { "permissionSlug is required" }
    require(!(resourceId != null && resourceExternalId != null)) {
      "Cannot specify both resourceId and resourceExternalId"
    }
    require(!(resourceExternalId != null && resourceTypeSlug == null)) {
      "resourceTypeSlug is required when resourceExternalId is specified"
    }
  }
}
