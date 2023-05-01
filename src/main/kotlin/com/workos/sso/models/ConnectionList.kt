package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Connection] resources. This class is not meant to be
 * instantiated directly.
 *
 * @param data A list of [Connection].
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class ConnectionList
@JsonCreator constructor(
  @JvmField
  val data: List<Connection>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
