package com.workos.webhooks.models

import com.workos.usermanagement.models.EmailVerificationEventData

/**
 * Webhook Event for `email_verification.*` events.
 */
class EmailVerificationEvent(
  override val id: String,
  override val event: EventType,
  override val data: EmailVerificationEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
