package com.workos.webhooks.models

/**
 * Webhook Event for `organization_role.*` events.
 */
class OrganizationRoleWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: OrganizationRoleEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
