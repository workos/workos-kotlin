package com.workos.webhooks.models

import com.workos.organizations.models.Organization

/**
 * Webhook Event for `organization.updated`.
 */
class OrganizationUpdatedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: Organization,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)

