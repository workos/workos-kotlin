package com.workos.webhooks.models

import com.workos.roles.models.Role

/**
 * Webhook Event for `role.*` events.
 */
class RoleWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Role,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
