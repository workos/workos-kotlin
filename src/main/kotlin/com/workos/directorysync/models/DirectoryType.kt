package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of types for a [Directory].
 *
 * @param type The Directory Type string value.
 */
enum class DirectoryType(@JsonValue val type: String) {
  /**
   * Azure SCIM 2.0
   */
  AzureSCIMV2_0("azure scim v2.0"),
  /**
   * BambooHR
   */
  BambooHr("bamboohr"),
  /**
   * Breathe HR
   */
  BreatheHr("breathe hr"),
  /**
   * Cezanne HR
   */
  CezanneHr("cezanne hr"),
  /**
   * Cyberark SCIM 2.0
   */
  CyberarkSCIMV2_0("cyberark scim v2.0"),
  /**
   * FourthHR
   */
  FourthHr("fourth hr"),
  /**
   * Generic SCIM 1.1
   */
  GenericSCIMV1_1("generic scim v1.1"),
  /**
   * Generic SCIM 2.0
   */
  GenericSCIMV2_0("generic scim v2.0"),
  /**
   * Google Workspace https://workspace.google.com/
   */
  GSuiteDirectory("gsuite directory"),
  /**
   * Hibob https://www.hibob.com/
   */
  Hibob("hibob"),
  /**
   * JumpCloud SCIM https://jumpcloud.com/
   */
  JumpCloudSCIM2_0("jump cloud scim v2.0"),
  /**
   * Okta SCIM 1.1 https://developer.okta.com/docs/reference/scim/scim-11/
   */
  OktaSCIMV1_1("okta scim v1.1"),
  /**
   * Okta SCIM 2.0 https://developer.okta.com/docs/reference/scim/scim-20/
   */
  OktaSCIMV2_0("okta scim v2.0"),
  /**
   * OneLogin SCIM 2.0 https://developers.onelogin.com/scim
   */
  OneLoginSCIMV2_0("onelogin scim v2.0"),
  /**
   * PeopleHR  https://peoplehr.com/
   */
  PeopleHR("people hr"),
  /**
   * Persionio  https://www.personio.com/
   */
  Personio("personio"),
  /**
   * PingFederate SCIM 2.0  https://pingfederate.com/
   */
  PingFederateSCIMV2_0("pingfederate scim v2.0"),
  /**
   * Rippling https://www.rippling.com/
   */
  Rippling("rippling scim v2.0"),
  /**
   * Workday https://www.workday.com/
   */
  Workday("workday"),
}
