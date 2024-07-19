package com.workos.webhooks.models

import com.workos.usermanagement.models.PasswordResetEventData

/**
 * Webhook Event for `password_reset.*` events.
 */
class PasswordResetEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: PasswordResetEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
