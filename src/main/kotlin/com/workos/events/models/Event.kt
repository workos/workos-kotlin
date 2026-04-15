package com.workos.events.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Represents a WorkOS Event. Use subclasses for strongly-typed `data`.
 */
@JsonDeserialize(using = EventsJsonDeserializer::class)
abstract class Event(
  open val id: String,
  open val event: String,
  @JsonProperty("created_at")
  open val createdAt: String,
  open val context: Map<String, Any>? = null,
  @JsonProperty("object")
  open val obj: String = "event"
)
