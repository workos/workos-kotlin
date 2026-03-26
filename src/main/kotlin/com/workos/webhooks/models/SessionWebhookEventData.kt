package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.events.models.SessionImpersonator

/**
 * Data payload for `session.*` webhook events.
 */
data class SessionWebhookEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "session",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JvmField
  val impersonator: SessionImpersonator? = null,

  @JvmField
  @JsonProperty("ip_address")
  val ipAddress: String? = null,

  @JvmField
  @JsonProperty("user_agent")
  val userAgent: String? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
