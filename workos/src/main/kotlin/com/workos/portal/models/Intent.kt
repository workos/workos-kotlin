package com.workos.portal.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Intent of the Admin Portal.
 *
 * @param value The string value of the intent.
 */
enum class Intent(@JsonValue val value: String) {
  /**
   * Single sign on.
   */
  Sso("sso"),

  /**
   * Directory Sync.
   */
  DirectorySync("dsync")
}
