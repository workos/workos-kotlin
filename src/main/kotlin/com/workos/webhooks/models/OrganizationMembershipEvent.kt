package com.workos.webhooks.models

import com.workos.usermanagement.models.OrganizationMembership

/**
 * Webhook Event for `organization_membership.*` events.
 */
class OrganizationMembershipEvent(
  override val id: String,
  override val event: EventType,
  override val data: OrganizationMembership,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
