package com.workos.webhooks.models

/**
 * Webhook Event for `connection.saml_certificate_renewed`.
 */
class ConnectionSamlCertificateRenewedEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: SamlCertificateRenewedEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
