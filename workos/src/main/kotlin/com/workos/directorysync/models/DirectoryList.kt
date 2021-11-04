package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Directory] resources. This class is not meant to be
 * instantiated directly.
 *
 * @param data A list of [Directory].
 * @param listMetadata [com.workos.common.models.ListMetadata].
 */
data class DirectoryList
@JsonCreator constructor(
  @JvmField
  val data: List<Directory>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
