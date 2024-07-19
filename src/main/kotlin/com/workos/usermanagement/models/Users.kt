package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [User]s
 *
 * @param data A list of [User]s.
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class Users
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<User>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
