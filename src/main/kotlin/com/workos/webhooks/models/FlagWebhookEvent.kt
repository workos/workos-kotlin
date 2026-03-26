package com.workos.webhooks.models

/**
 * Webhook Event for `flag.*` events.
 */
class FlagWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: FlagEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
