package com.workos.webhooks.models

import com.workos.directorysync.models.Group

/**
 * Webhook Event for `dsync.group.created`.
 */
class DirectoryGroupCreatedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Group,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
