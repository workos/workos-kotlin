package com.workos.webhooks.models

import com.workos.usermanagement.models.InvitationEventData

/**
 * Webhook Event for `invitation.*` events.
 */
class InvitationEvent(
  override val id: String,
  override val event: EventType,
  override val data: InvitationEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
