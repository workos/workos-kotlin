package com.workos.webhooks.models

import com.workos.usermanagement.models.MagicAuthEventData

/**
 * Webhook Event for `magic_auth.*` events.
 */
class MagicAuthEvent(
  override val id: String,
  override val event: EventType,
  override val data: MagicAuthEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
