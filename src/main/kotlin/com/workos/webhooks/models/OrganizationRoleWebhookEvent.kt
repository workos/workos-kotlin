package com.workos.webhooks.models

/**
 * Webhook Event for `organization_role.*` events.
 */
class OrganizationRoleWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: OrganizationRoleEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
