package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ConnectionDomain
@JsonCreator constructor(
  @JvmField
  val domain: String,

  @JvmField
  val id: String,
)

data class Connection
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String,

  @JvmField
  @JsonProperty("connection_type")
  val connectionType: ConnectionType,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  val domains: List<ConnectionDomain>,

  @JvmField
  @JsonProperty("environment_id")
  val environmentId: String?,

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  val state: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,
)
