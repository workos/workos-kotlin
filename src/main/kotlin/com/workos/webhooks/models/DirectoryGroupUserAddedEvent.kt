package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Webhook Event for `dsync.group.user_added`.
 */
class DirectoryGroupUserAddedEvent @JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: DirectoryGroupUserEvent,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
