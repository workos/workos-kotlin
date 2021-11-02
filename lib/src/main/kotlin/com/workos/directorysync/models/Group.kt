package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Group
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "directory_group",

  @JvmField
  @JsonProperty("directory_id")
  val directoryId: String,

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any>,
)
