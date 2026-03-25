package com.workos.webhooks.models

/**
 * Webhook Event for `session.*` events.
 */
class SessionWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: SessionWebhookEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
