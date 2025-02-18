package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.organizations.types.OrganizationDomainState
import com.workos.organizations.types.OrganizationDomainVerificationStrategy

/**
 * An Organization Domain (also known as a User Email Domain) represents an Organization's domain.
 *
 * These domains restrict which email addresses are able to sign in through SAML Connections when
 * `allow_profiles_outside_organization` is `false`. This is the default behavior for Organizations.
 *
 * @param obj The unique object identifier type of the record.
 * @param id Unique identifier for the Organization Domain.
 * @param domain Domain for the Organization Domain.
 */
data class OrganizationDomain
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_domain",

  @JvmField
  val id: String,

  @JvmField
  val domain: String,

  @JvmField
  @JsonProperty("state")
  val state: OrganizationDomainState? = null,

  @JvmField
  @JsonProperty("verification_strategy")
  val verificationStrategy: OrganizationDomainVerificationStrategy? = null,

  @JvmField
  @JsonProperty("verification_token")
  val verificationToken: String? = null
)
