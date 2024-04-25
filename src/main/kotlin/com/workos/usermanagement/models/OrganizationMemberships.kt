package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [OrganizationMembership]s
 *
 * @param data A list of [OrganizationMembership]s ordered by creation time.
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class OrganizationMemberships
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<OrganizationMembership>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
