package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for `permission.*` webhook events.
 */
data class PermissionEventData(
  @JvmField
  @JsonProperty("object")
  val obj: String = "permission",
  @JvmField
  val id: String,
  @JvmField
  val slug: String,
  @JvmField
  val name: String,
  @JvmField
  val description: String? = null,
  @JvmField
  val system: Boolean = false,
  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,
  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
