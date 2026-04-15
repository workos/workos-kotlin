package com.workos.webhooks.models

import com.workos.usermanagement.models.AuthenticationEventData

/**
 * Webhook Event for `authentication.*` events.
 */
class AuthenticationEvent(
  override val id: String,
  override val event: EventType,
  override val data: AuthenticationEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
