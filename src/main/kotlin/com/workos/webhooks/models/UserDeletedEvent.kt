package com.workos.webhooks.models

import com.workos.usermanagement.models.User

/**
 * Webhook Event for `user.deleted`.
 */
class UserDeletedEvent(
  override val id: String,
  override val event: EventType,
  override val data: User,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
