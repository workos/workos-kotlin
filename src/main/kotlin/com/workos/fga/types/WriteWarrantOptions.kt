package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.fga.models.Subject

@JsonInclude(JsonInclude.Include.NON_NULL)
class WriteWarrantOptions @JvmOverloads constructor(
  /**
   * Operation to perform for the given warrant (create/delete).
   */
  @JsonProperty("op")
  val op: String,

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
   * A boolean expression that must evaluate to true for this warrant to apply. The expression can reference variables provided in the context attribute of access check requests.
   */
  @JsonProperty("policy")
  val policy: String? = null,
) {
  init {
    require(resourceType.isNotBlank()) { "Resource type is required" }
    require(resourceId.isNotBlank()) { "Resource id is required" }
    require(relation.isNotBlank()) { "Relation is required" }
    require(subject.resourceType.isNotBlank() && subject.resourceId.isNotBlank()) { "Subject is required" }
  }
}
