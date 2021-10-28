package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class DirectoryGroupList
@JsonCreator constructor(
  val data: List<Group>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
