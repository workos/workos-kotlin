package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for `flag.*` webhook events.
 */
data class FlagEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "feature_flag",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("environment_id")
  val environmentId: String,

  @JvmField
  val slug: String,

  @JvmField
  val name: String,

  @JvmField
  val description: String? = null,

  @JvmField
  val tags: List<String> = emptyList(),

  @JvmField
  val enabled: Boolean,

  @JvmField
  @JsonProperty("default_value")
  val defaultValue: Boolean,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
