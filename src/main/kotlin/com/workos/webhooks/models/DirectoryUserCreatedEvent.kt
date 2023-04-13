package com.workos.webhooks.models

import com.workos.directorysync.models.User

/**
 * Webhook Event for `dsync.user.created`.
 */
class DirectoryUserCreatedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: User,

  @JvmField
  override val created_at: String
) : WebhookEvent(id, event, data)
