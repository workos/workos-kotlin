package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.directorysync.models.Group

/**
 * Webhook Event for `dsync.group.deleted`.
 */
class DirectoryGroupDeletedEvent @JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Group,

  @JvmField
  override val created_at: String
) : WebhookEvent(id, event, data)
