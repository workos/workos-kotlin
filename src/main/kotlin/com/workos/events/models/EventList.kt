package com.workos.events.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

/**
 * A list of WorkOS [Event] resources.
 */
data class EventList
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "list",

  @JvmField
  val data: List<Event>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
