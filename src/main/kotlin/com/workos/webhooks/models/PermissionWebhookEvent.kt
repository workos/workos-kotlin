package com.workos.webhooks.models

/**
 * Webhook Event for `permission.*` events.
 */
class PermissionWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: PermissionEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
