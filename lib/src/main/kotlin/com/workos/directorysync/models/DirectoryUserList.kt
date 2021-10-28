package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class DirectoryUserList
@JsonCreator constructor(
  var data: List<User>,

  @JsonProperty("list_metadata")
  var listMetadata: ListMetadata
)
