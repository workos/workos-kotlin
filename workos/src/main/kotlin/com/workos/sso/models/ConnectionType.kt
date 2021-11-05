package com.workos.sso.models

/**
 * An enumeration of types for a [Connection].
 *
 * @param state The Connection Type string value.
 */
enum class ConnectionType(val type: String) {
  /**
   * Azure Directory Federation Services SAML
   */
  ADFSSAML("ADFSSAML"),
  /**
   * Auth0 SAML Integration
   */
  Auth0SAML("Auth0SAML"),
  /**
   * Azure SAML
   */
  AzureSAML("AzureSAML"),
  /**
   * CyberArk SAML
   */
  CyberArkSAML("CyberArkSAML"),
  /**
   * Duo SAML
   */
  DuoSAML("DuoSAML"),
  /**
   * Generic OIDC
   */
  GenericOIDC("GenericOIDC"),
  /**
   * Generic SAML
   */
  GenericSAML("GenericSAML"),
  /**
   * Google OAuth
   */
  GoogleOAuth("GoogleOAuth"),
  /**
   * Google SAML
   */
  GoogleSAML("GoogleSAML"),
  /**
   * JumpCloud SAML
   */
  JumpCloudSAML("JumpCloudSAML"),
  /**
   * Magic Link
   */
  MagicLink("MagicLink"),
  /**
   * Microsoft OAuth
   */
  MicrosoftOAuth("MicrosoftOAuth"),
  /**
   * Okta SAML
   */
  OktaSAML("OktaSAML"),
  /**
   * OneLogin SAML
   */
  OneLoginSAML("OneLoginSAML"),
  /**
   * Ping Federate SAML
   */
  PingFederateSAML("PingFederateSAML"),
  /**
   * Ping One SAML
   */
  PingOneSAML("PingOneSAML"),
  /**
   * Rippling SAML
   */
  RipplingSAML("RipplingSAML"),
  /**
   * Salesforce SAML
   */
  SalesforceSAML("SalesforceSAML"),
  /**
   * Shibboleth SAML
   */
  ShibbolethSAML("ShibbolethSAML"),
  /**
   * VMware SAML
   */
  VMwareSAML("VMwareSAML"),
}
