package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of [Invitation]s
 *
 * @param data A list of [Invitation]s order by creation time
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class Invitations
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<Invitation>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
