package com.workos.webhooks.models

import com.workos.directorysync.models.User

/**
 * Webhook Event for `dsync.user.created`.
 */
class DirectoryUserCreatedEvent(
  override val id: String,
  override val event: EventType,
  override val data: User,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
