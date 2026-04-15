package com.workos.webhooks.models

/**
 * Webhook Event for `dsync.user.updated`.
 */
class DirectoryUserUpdatedEvent(
  override val id: String,
  override val event: EventType,
  override val data: UserUpdated,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
