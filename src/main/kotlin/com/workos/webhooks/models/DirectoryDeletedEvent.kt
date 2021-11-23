package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.directorysync.models.Directory

/**
 * Webhook Event for `dsync.deleted`.
 */
class DirectoryDeletedEvent @JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Directory
) : WebhookEvent(id, event, data)
