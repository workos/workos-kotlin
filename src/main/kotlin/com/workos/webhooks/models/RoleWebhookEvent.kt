package com.workos.webhooks.models

import com.workos.roles.models.Role

/**
 * Webhook Event for `role.*` events.
 */
class RoleWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: Role,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
