package com.workos.webhooks.models

/**
 * Webhook Event for `flag.*` events.
 */
class FlagWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: FlagEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
