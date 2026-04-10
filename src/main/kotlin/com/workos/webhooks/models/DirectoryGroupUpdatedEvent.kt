package com.workos.webhooks.models

/**
 * Webhook Event for `dsync.group.updated`.
 */
class DirectoryGroupUpdatedEvent(
  override val id: String,
  override val event: EventType,
  override val data: GroupUpdated,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
