package com.workos.sso.models

enum class ConnectionType(val type: String) {
  ADFSSAML("ADFSSAML"),
  Auth0SAML("Auth0SAML"),
  AzureSAML("AzureSAML"),
  CyberArkSAML("CyberArkSAML"),
  DuoSAML("DuoSAML"),
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
