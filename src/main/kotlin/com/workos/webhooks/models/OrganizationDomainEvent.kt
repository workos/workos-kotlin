package com.workos.webhooks.models

import com.workos.organizations.models.OrganizationDomain

/**
 * Webhook Event for `organization_domain.*` events.
 */
class OrganizationDomainEvent(
  override val id: String,
  override val event: EventType,
  override val data: OrganizationDomain,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
