package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Resource]s
 *
 * @param data A list of [Resource]s.
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class Resources
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<Resource>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
