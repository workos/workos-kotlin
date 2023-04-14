package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.directorysync.models.Directory

/**
 * Webhook Event for `dsync.activated`.
 */
class DirectoryActivatedEvent @JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Directory,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
