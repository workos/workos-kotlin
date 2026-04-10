package com.workos.webhooks.models

/**
 * Webhook Event for `dsync.group.user_removed`.
 */
class DirectoryGroupUserRemovedEvent(
  override val id: String,
  override val event: EventType,
  override val data: DirectoryGroupUserEvent,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
