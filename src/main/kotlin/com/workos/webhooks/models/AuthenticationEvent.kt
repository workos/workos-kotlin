package com.workos.webhooks.models

import com.workos.usermanagement.models.AuthenticationEventData

/**
 * Webhook Event for `authentication.*` events.
 */
class AuthenticationEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: AuthenticationEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
