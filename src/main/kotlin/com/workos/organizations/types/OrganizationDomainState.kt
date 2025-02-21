package com.workos.organizations.types

import com.fasterxml.jackson.annotation.JsonValue

enum class OrganizationDomainState(@JsonValue val type: String) {
  /**
   * Failed
   */
  Failed("failed"),

  /**
   * Legacy Verified
   */
  LegacyVerified("legacy_verified"),

  /**
   * Pending
   */
  Pending("pending"),

  /**
   * Verified
   */
  Verified("verified");
}
