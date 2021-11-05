package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Directory Group resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param directoryId The unique identifier for the [Directory] the Group belongs to.
 * @param id The unique identifier for the Directory Group
 * @param name The name of the Directory Group.
 * @param rawAttributes An object containing the data returned from the Directory Provider.
 */
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
