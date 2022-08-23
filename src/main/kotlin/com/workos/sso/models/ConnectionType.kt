package com.workos.sso.models

/**
 * An enumeration of types for a [Connection].
 *
 * @param type The Connection Type string value.
 */
enum class ConnectionType(val type: String) {
  /**
   * Azure Directory Federation Services SAML
   */
  ADFSSAML("ADFSSAML"),
  /**
   * ADP OIDC
   */
  AdpOidc("AdpOidc"),
  /**
   * Auth0 SAML Integration
   */
  Auth0SAML("Auth0SAML"),
  /**
   * Azure SAML
   */
  AzureSAML("AzureSAML"),
  /**
   * Cas SAML
   */
  CasSAML("CasSAML"),
  /**
   * Cloudflare SAML
   */
  CloudflareSAML("CloudflareSAML"),
  /**
   * Classlink SAML
   */
  ClassLink("ClassLinkSAML"),
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
   * KeyCloak SAML
   */
  KeyCloakSAML("KeycloakSAML"),
  /**
   * Magic Link
   */
  MagicLink("MagicLink"),
  /**
   * Microsoft OAuth
   */
  MicrosoftOAuth("MicrosoftOAuth"),
  /**
   * MiniOrange SAML
   */
  MiniOrangeSAML("MiniOrangeSAML"),
  /**
   * NetIQ SAML
   */
  NetIQSAML("NetIQSAML"),
  /**
   * Okta SAML
   */
  OktaSAML("OktaSAML"),
  /**
   * OneLogin SAML
   */
  OneLoginSAML("OneLoginSAML"),
  /**
   * Oracle SAML
   */
  OracleSAML("OracleSAML"),
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
   * Shibboleth Generic SAML
   */
  ShibbolethGenericSAML("ShibbolethGenericSAML"),
  /**
   * Shibboleth SAML
   */
  ShibbolethSAML("ShibbolethSAML"),
  /**
   * SimpleSAMLPhp SAML
   */
  SimpleSamlPhpSAML("SimpleSamlPhpSAML"),
  /**
   * VMware SAML
   */
  VMwareSAML("VMwareSAML"),
}
