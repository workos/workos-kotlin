package com.workos.webhooks.models

/**
 * Webhook Event for `permission.*` events.
 */
class PermissionWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: PermissionEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
