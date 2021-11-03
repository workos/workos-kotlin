package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Profile
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String,

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("idp_id")
  val idpId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  @JsonProperty("connection_id")
  val connectionId: String,

  @JvmField
  @JsonProperty("connection_type")
  val connectionType: ConnectionType,

  @JvmField
  val email: String,

  @JvmField
  @JsonProperty("first_name")
  val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  val lastName: String?,

  @JvmField
  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any>,
)
