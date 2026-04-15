package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of types for a [Directory].
 *
 * @param type The Directory Type string value.
 */
enum class DirectoryType(
  @JsonValue val type: String
) {
  /**
   * Azure SCIM 2.0
   */
  AzureScimV20("azure scim v2.0"),

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
  CyberarkScimV20("cyberark scim v2.0"),

  /**
   * FourthHR
   */
  FourthHr("fourth hr"),

  /**
   * Generic SCIM 2.0
   */
  GenericScimV20("generic scim v2.0"),

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
  JumpCloudScimV20("jump cloud scim v2.0"),

  /**
   * Okta SCIM 2.0 https://developer.okta.com/docs/reference/scim/scim-20/
   */
  OktaScimV20("okta scim v2.0"),

  /**
   * OneLogin SCIM 2.0 https://developers.onelogin.com/scim
   */
  OneLoginScimV20("onelogin scim v2.0"),

  /**
   * PeopleHR  https://peoplehr.com/
   */
  PeopleHR("people hr"),

  /**
   * Personio  https://www.personio.com/
   */
  Personio("personio"),

  /**
   * PingFederate SCIM 2.0  https://pingfederate.com/
   */
  PingFederateScimV20("pingfederate scim v2.0"),

  /**
   * Rippling https://www.rippling.com/
   */
  Rippling("rippling scim v2.0"),

  /**
   * SFTP
   */
  SFTP("sftp"),

  /**
   * SFTP Workday
   */
  SFTPWorkday("sftp workday"),

  /**
   * Workday https://www.workday.com/
   */
  Workday("workday"),

  /**
   * An unknown directory type.
   */
  @JsonEnumDefaultValue
  Unknown("unknown")
}
