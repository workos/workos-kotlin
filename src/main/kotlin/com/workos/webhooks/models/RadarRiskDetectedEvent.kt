package com.workos.webhooks.models

/**
 * Webhook Event for `authentication.radar_risk_detected`.
 *
 * This event has a unique payload structure different from other authentication events.
 */
class RadarRiskDetectedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: RadarRiskDetectedEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
