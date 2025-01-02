package com.workos.roles.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkOS Organization resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id The unique identifier for the Role.
 * @param name The name of the Role.
 * @param slug The slug of the Role.
 * @param description The description of the Role.
 * @param type The type of the Role.
 * @param createdAt The timestamp of when the Role was created.
 * @param updatedAt The timestamp of when the Role was updated.
 */
data class Role
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "role",

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  val slug: String,

  @JvmField
  val description: String? = null,

  @JvmField
  val type: RoleType,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
