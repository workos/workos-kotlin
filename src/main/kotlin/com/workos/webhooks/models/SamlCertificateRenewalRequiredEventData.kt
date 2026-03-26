package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for the `connection.saml_certificate_renewal_required` webhook event.
 */
data class SamlCertificateRenewalRequiredEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  val connection: SamlCertificateConnection,

  @JvmField
  val certificate: SamlCertificateInfo,

  @JvmField
  @JsonProperty("days_until_expiry")
  val daysUntilExpiry: Int
)

/**
 * Connection reference within a SAML certificate event.
 */
data class SamlCertificateConnection
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String? = null
)

/**
 * SAML certificate details within a certificate event.
 */
data class SamlCertificateInfo
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("certificate_type")
  val certificateType: String,

  @JvmField
  @JsonProperty("expiry_date")
  val expiryDate: String,

  @JvmField
  @JsonProperty("is_expired")
  val isExpired: Boolean? = null
)
