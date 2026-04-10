package com.workos.webhooks.models

/**
 * Webhook Event for `connection.saml_certificate_renewal_required`.
 */
class ConnectionSamlCertificateRenewalRequiredEvent(
  override val id: String,
  override val event: EventType,
  override val data: SamlCertificateRenewalRequiredEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
