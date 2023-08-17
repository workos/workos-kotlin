package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Session @JsonCreator constructor(

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("token")
  val token: String,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("expires_at")
  val expiresAt: String,

  @JvmField
  @JsonProperty("authorized_organizations")
  val authorizedOrganizations: List<AuthorizedOrganization>? = null,

  @JvmField
  @JsonProperty("unauthorized_organization")
  val unauthorizedOrganizations: List<UnauthorizedOrganization>? = null,
)

