package com.workos.webhooks.models

import com.workos.usermanagement.models.EmailVerificationEventData

/**
 * Webhook Event for `email_verification.*` events.
 */
class EmailVerificationEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: EmailVerificationEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
