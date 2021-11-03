package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ConnectionDomain
@JsonCreator constructor(
  @JvmField
  val domain: String,

  @JvmField
  val id: String,
)

data class Connection
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String,

  @JvmField
  @JsonProperty("connection_type")
  val connectionType: ConnectionType,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  val domains: List<ConnectionDomain>,

  @JvmField
  @JsonProperty("environment_id")
  val environmentId: String?,

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  val state: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,
)

enum class ConnectionType(val type: String) {
  ADFSSAML("ADFSSAML"),
  Auth0SAML("Auth0SAML"),
  AzureSAML("AzureSAML"),
  CyberArkSAML("CyberArkSAML"),
  GenericOIDC("GenericOIDC"),
  GenericSAML("GenericSAML"),
  GoogleOAuth("GoogleOAuth"),
  GoogleSAML("GoogleSAML"),
  JumpCloudSAML("JumpCloudSAML"),
  MagicLink("MagicLink"),
  MicrosoftOAuth("MicrosoftOAuth"),
  OktaSAML("OktaSAML"),
  OneLoginSAML("OneLoginSAML"),
  PingFederateSAML("PingFederateSAML"),
  PingOneSAML("PingOneSAML"),
  RipplingSAML("RipplingSAML"),
  SalesforceSAML("SalesforceSAML"),
  ShibbolethSAML("ShibbolethSAML"),
  VMwareSAML("VMwareSAML"),
}
