package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for the `authentication.radar_risk_detected` webhook event.
 */
data class RadarRiskDetectedEventData
@JsonCreator constructor(
  @JvmField
  @JsonProperty("auth_method")
  val authMethod: String,

  @JvmField
  val action: String,

  @JvmField
  val control: String? = null,

  @JvmField
  @JsonProperty("blocklist_type")
  val blocklistType: String? = null,

  @JvmField
  @JsonProperty("ip_address")
  val ipAddress: String? = null,

  @JvmField
  @JsonProperty("user_agent")
  val userAgent: String? = null,

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  val email: String
)
