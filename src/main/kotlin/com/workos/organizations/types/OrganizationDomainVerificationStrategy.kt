package com.workos.organizations.types

import com.fasterxml.jackson.annotation.JsonValue

enum class OrganizationDomainVerificationStrategy(@JsonValue val type: String) {
  /**
   * DNS
   */
  Dns("dns"),

  /**
   * Manual
   */
  Manual("manual")
}
