package com.workos.webhooks.models

import com.workos.directorysync.models.User

/**
 * Webhook Event for `dsync.user.deleted`.
 */
class DirectoryUserDeletedEvent(
  override val id: String,
  override val event: EventType,
  override val data: User,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
