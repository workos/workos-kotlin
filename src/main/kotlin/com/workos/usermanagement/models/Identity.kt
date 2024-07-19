package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.types.IdentityProviderEnumType

/**
 * Represents `User` identities obtained from external identity providers.
 *
 * @param idpId The unique ID of the invitation.
 * @param type The email address of the user.
 * @param provider The state of the invitation (see enum values in [IdentityProviderEnumType]).
 */
data class Identity @JsonCreator constructor(
  @JsonProperty("idp_id")
  val idpId: String,

  @JsonProperty("type")
  val type: String = "OAuth",

  @JsonProperty("provider")
  val provider: IdentityProviderEnumType,
)
