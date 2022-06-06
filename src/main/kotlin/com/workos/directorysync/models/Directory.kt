package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkOS Directory resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id The unique identifier for the Directory.
 * @param name The name of the Directory.
 * @param domain The URL associated with an Enterprise Client.
 * @param externalKey Externally used identifier for the Directory.
 * @param organizationId Identifier for the Directory's Organization.
 * @param state The state of the Directory.
 * @param type The type of the Directory.
 * @param createdAt The timestamp of when the Directory was created.
 * @param updatedAt The timestamp of when the Directory was updated.
 */
data class Directory
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "directory",

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  val domain: String?,

  @JvmField
  @JsonProperty("external_key")
  val externalKey: String?,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  val state: DirectoryState,

  @JvmField
  val type: DirectoryType,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,
)
