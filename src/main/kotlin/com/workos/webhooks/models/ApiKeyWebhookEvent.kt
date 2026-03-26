package com.workos.webhooks.models

/**
 * Webhook Event for `api_key.*` events.
 */
class ApiKeyWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: ApiKeyEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
