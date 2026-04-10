package com.workos.webhooks.models

import com.workos.directorysync.models.Directory

/**
 * Webhook Event for `dsync.activated`.
 */
class DirectoryActivatedEvent(
  override val id: String,
  override val event: EventType,
  override val data: Directory,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
