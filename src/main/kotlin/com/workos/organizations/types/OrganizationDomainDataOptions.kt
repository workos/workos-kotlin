package com.workos.organizations.types

/**
  * Options when setting the domains for an organization.
  *
  * @param domain The domain of the organization.
  * @param state The verificaction state of the domain.
  */
class OrganizationDomainDataOptions @JvmOverloads constructor(
  val domain: String,

  val state: OrganizationDomainDataState
)
