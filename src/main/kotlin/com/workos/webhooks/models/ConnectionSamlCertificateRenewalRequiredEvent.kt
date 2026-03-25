package com.workos.webhooks.models

/**
 * Webhook Event for `connection.saml_certificate_renewal_required`.
 */
class ConnectionSamlCertificateRenewalRequiredEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: SamlCertificateRenewalRequiredEventData,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
