package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Group
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory_group",

  @JsonProperty("directory_id")
  val directoryId: String?,

  val id: String,

  val name: String,

  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, String>?,
)
