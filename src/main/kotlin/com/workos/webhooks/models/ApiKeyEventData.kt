package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for `api_key.*` webhook events.
 */
data class ApiKeyEventData(
  @JvmField
  @JsonProperty("object")
  val obj: String = "api_key",
  @JvmField
  val id: String,
  @JvmField
  val name: String,
  @JvmField
  @JsonProperty("obfuscated_value")
  val obfuscatedValue: String,
  @JvmField
  @JsonProperty("last_used_at")
  val lastUsedAt: String? = null,
  @JvmField
  val permissions: List<String> = emptyList(),
  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,
  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
