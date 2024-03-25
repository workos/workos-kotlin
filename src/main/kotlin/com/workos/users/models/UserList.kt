package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [User]
 *
 * @param data A list of [User].
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class UserList
@JsonCreator constructor(
  @JvmField
  val data: List<User>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
