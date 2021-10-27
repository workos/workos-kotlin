package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Group
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory_group",
  var id: String,
  var name: String,
)
