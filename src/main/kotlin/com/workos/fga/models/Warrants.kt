package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Warrant]s
 *
 * @param data A list of [Warrant]s.
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class Warrants
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<Warrant>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
