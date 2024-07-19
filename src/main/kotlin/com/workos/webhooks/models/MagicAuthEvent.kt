package com.workos.webhooks.models

import com.workos.usermanagement.models.MagicAuthEventData

/**
 * Webhook Event for `magic_auth.*` events.
 */
class MagicAuthEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: MagicAuthEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
