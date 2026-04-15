package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Directory Group resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param directoryId The unique identifier for the [Directory] the Group belongs to.
 * @param organizationId The identifier for the Organization in which the Directory resides.
 * @param id The unique identifier for the Directory Group
 * @param idpId Unique identifier for the group, assigned by the Directory Provider. Different Directory Providers use different ID formats.
 * @param name The name of the Directory Group.
 * @param rawAttributes An object containing the data returned from the Directory Provider.
 */
open class Group(
  @JsonProperty("object")
  open val obj: String = "directory_group",
  @JsonProperty("directory_id")
  open val directoryId: String,
  @JsonProperty("organization_id")
  open val organizationId: String?,
  open val id: String,
  @JsonProperty("idp_id")
  open val idpId: String,
  open val name: String,
  @JsonProperty("created_at")
  open val createdAt: String,
  @JsonProperty("updated_at")
  open val updatedAt: String,
  @JsonProperty("raw_attributes")
  open val rawAttributes: Map<String, Any?>
)
