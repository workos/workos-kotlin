package com.workos.sso.models

/**
 * An enumeration of types for a [Factor].
 *
 * @param type The Factor Type string value.
 */
enum class MfaType(val type: String) {
  /**
   * Generic otp type.
   */
  Generic("generic_otp"),
  /**
   * Totp type.
   */
  Totp("totp"),
  /**
   * Sms type.
   */
  Sms("sms"),
}