package com.workos.webhooks.models

/**
 * Webhook Event for `api_key.*` events.
 */
class ApiKeyWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: ApiKeyEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
