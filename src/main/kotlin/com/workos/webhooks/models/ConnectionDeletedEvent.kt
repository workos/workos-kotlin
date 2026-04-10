package com.workos.webhooks.models

import com.workos.sso.models.Connection

/**
 * Webhook Event for `connection.deleted`.
 */
class ConnectionDeletedEvent(
  override val id: String,
  override val event: EventType,
  override val data: Connection,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
