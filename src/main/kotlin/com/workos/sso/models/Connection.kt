package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkOS Connection resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id Unique identifier for the Connection.
 * @param organizationId Unique identifier for the Organization in which the Connection resides.
 * @param connectionType The type of SSO Connection used to authenticate a user.
 * @param domains Domain records for the Connection.
 * @param environmentId The environment id that the Connection is associated with.
 * @param name A human-readable name for the Connection. This will most commonly be the Enterprise Client's name.
 * @param state The state of the Connection.
 * @param createdAt The timestamp of when the Connection was created.
 * @param updatedAt The timestamp of when the Connection was updated.
 */
data class Connection
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "connection",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  @JsonProperty("connection_type")
  val connectionType: ConnectionType,

  @JvmField
  val domains: List<ConnectionDomain>?,

  @JvmField
  @JsonProperty("environment_id")
  val environmentId: String?,

  @JvmField
  val name: String,

  @JvmField
  val state: ConnectionState,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,
)
