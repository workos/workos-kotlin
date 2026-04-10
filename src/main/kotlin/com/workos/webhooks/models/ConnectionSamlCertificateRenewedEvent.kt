package com.workos.webhooks.models

/**
 * Webhook Event for `connection.saml_certificate_renewed`.
 */
class ConnectionSamlCertificateRenewedEvent(
  override val id: String,
  override val event: EventType,
  override val data: SamlCertificateRenewedEventData,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
