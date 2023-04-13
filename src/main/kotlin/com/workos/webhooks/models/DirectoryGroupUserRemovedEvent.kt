package com.workos.webhooks.models

/**
 * Webhook Event for `dsync.group.user_removed`.
 */
class DirectoryGroupUserRemovedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: DirectoryGroupUserEvent,

  @JvmField
  override val created_at: String
) : WebhookEvent(id, event, data)
