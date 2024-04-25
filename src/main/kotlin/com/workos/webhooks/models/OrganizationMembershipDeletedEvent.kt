package com.workos.webhooks.models

import com.workos.usermanagement.models.OrganizationMembership

/**
 * Webhook Event for `organization_membership.deleted`.
 */
class OrganizationMembershipDeletedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: OrganizationMembership,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
