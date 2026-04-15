package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for the `connection.saml_certificate_renewed` webhook event.
 */
data class SamlCertificateRenewedEventData(
  @JvmField
  val connection: SamlCertificateConnection,
  @JvmField
  val certificate: SamlCertificateInfo,
  @JvmField
  @JsonProperty("renewed_at")
  val renewedAt: String
)
