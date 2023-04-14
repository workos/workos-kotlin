package com.workos.webhooks.models

/**
 * Webhook Event for `dsync.user.updated`.
 */
class DirectoryUserUpdatedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: UserUpdated,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
