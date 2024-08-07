package com.workos.webhooks.models

import com.workos.usermanagement.models.InvitationEventData

/**
 * Webhook Event for `invitation.*` events.
 */
class InvitationEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: InvitationEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
