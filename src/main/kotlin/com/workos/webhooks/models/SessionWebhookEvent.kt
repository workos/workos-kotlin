package com.workos.webhooks.models

/**
 * Webhook Event for `session.*` events.
 */
class SessionWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: SessionWebhookEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
