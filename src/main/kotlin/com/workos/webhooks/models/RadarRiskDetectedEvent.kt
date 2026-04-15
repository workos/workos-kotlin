package com.workos.webhooks.models

/**
 * Webhook Event for `authentication.radar_risk_detected`.
 *
 * This event has a unique payload structure different from other authentication events.
 */
class RadarRiskDetectedEvent(
  override val id: String,
  override val event: EventType,
  override val data: RadarRiskDetectedEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
