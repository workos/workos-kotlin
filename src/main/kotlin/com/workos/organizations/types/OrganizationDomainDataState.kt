package com.workos.organizations.types

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The verification state of the domain
 *
 * @property Pending The domain is pending verification.
 * @property Verified The domain has been verified. Pass this if you've already verified the domain through other methods.
 */
enum class OrganizationDomainDataState {
  @JsonProperty("verified")
  Verified,

  @JsonProperty("pending")
  Pending
}
