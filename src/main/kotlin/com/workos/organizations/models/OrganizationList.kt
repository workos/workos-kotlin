package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Organization] resources. This class is not meant to be
 * instantiated directly.
 *
 * @param data A list of [Organization].
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class OrganizationList(
  @JvmField
  val data: List<Organization>,
  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
