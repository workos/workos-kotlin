package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of authentication factors
 *
 * @param data A list of [AuthenticationFactor]s ordered by creation time.
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class AuthenticationFactors
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<AuthenticationFactor>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
