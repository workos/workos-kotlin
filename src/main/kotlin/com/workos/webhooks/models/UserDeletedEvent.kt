package com.workos.webhooks.models

import com.workos.usermanagement.models.User

/**
 * Webhook Event for `user.deleted`.
 */
class UserDeletedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: User,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
