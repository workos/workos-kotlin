package com.workos.webhooks.models

import com.workos.directorysync.models.Group

/**
 * Webhook Event for `dsync.group.created`.
 */
class DirectoryGroupCreatedEvent(
  override val id: String,
  override val event: EventType,
  override val data: Group,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
