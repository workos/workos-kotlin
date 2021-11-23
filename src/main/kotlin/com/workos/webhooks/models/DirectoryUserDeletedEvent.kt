package com.workos.webhooks.models

import com.workos.directorysync.models.User

/**
 * Webhook Event for `dsync.user.deleted`.
 */
class DirectoryUserDeletedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: User
) : WebhookEvent(id, event, data)
