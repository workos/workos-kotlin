package com.workos.webhooks.models

import com.workos.usermanagement.models.PasswordResetEventData

/**
 * Webhook Event for `password_reset.*` events.
 */
class PasswordResetEvent(
  override val id: String,
  override val event: EventType,
  override val data: PasswordResetEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
