package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkoS Profile resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id Unique identifier for the Profile.
 * @param idpId Unique identifier for the user, assigned by the Identity Provider. Different Identity Providers use different ID formats. One possible use case for idp_id is associating a user’s SSO Profile with any relevant Directory Sync actions related to that user.
 * @param organizationId Identifier for the Profile's Organization.
 * @param connectionId Identifier for the Profile's Connection.
 * @param connectionType The type of SSO Connection used to authenticate a user.
 * @param email The user's email address.
 * @param firstName The user's first name.
 * @param lastName The user's last name.
 * @param groups The user's group memberships.
 * @param rawAttributes Object of key-value pairs containing relevant user data from the Identity Provider.
 */
data class Profile
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "profile",

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
  @JsonProperty("groups")
  val groups: List<String>?,

  @JvmField
  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any>,
)
