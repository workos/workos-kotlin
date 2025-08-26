package com.workos.events.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Represents a WorkOS Event. Use subclasses for strongly-typed `data`.
 */
@JsonDeserialize(using = EventsJsonDeserializer::class)
abstract class Event
@JsonCreator constructor(
  @JvmField
  open val id: String,

  @JvmField
  open val event: String,

  @JvmField
  @JsonProperty("created_at")
  open val createdAt: String,

  @JvmField
  open val context: Map<String, Any>? = null,

  @JvmField
  @JsonProperty("object")
  open val obj: String = "event"
)
