package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.sso.models.Connection

/**
 * Webhook Event for `connection.deactivated`.
 */
class ConnectionDeactivatedEvent
@JsonCreator constructor(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Connection,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
