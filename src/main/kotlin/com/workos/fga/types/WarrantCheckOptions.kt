package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.fga.models.Subject

class WarrantCheckOptions @JvmOverloads constructor(
  /**
   * The type of the resource.
   */
  @JsonProperty("resource_type")
  val resourceType: String,

  /**
   * The unique ID of the resource.
   */
  @JsonProperty("resource_id")
  val resourceId: String,

  /**
   * The relation for this resource to subject association. The relation must be valid per the resource type definition.
   */
  @JsonProperty("relation")
  val relation: String,

  /**
   * The resource with the specified `relation` to the resource provided by resourceType and resourceId.
   */
  @JsonProperty("subject")
  val subject: Subject,

  /**
   * Contextual data to use for resolving the access check. This data will be used when evaluating warrant policies.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("context")
  val context: Map<String, Any>? = null
) {
  init {
    require(resourceType.isNotBlank()) { "Resource type is required" }
    require(resourceId.isNotBlank()) { "Resource id is required" }
    require(relation.isNotBlank()) { "Relation is required" }
    require(subject.resourceType.isNotBlank() && subject.resourceId.isNotBlank()) { "Subject is required" }
  }
}
