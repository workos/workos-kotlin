package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A WorkOS warrant
 *
 * @param resourceType The type of the resource. Must be one of your system's existing resource types.
 * @param resourceId The unique ID of the resource.
 * @param relation The relation for this resource to subject association. The relation must be valid per the resource type definition.
 * @param subject (see [Subject]) The resource with the specified `relation` to the resource provided by resourceType and resourceId.
 * @param policy A boolean expression that must evaluate to true for this warrant to apply. The expression can reference variables provided in the context attribute of access check requests.
 */
data class Warrant @JsonCreator constructor(
  @JsonProperty("resource_type")
  val resourceType: String,

  @JsonProperty("resource_id")
  val resourceId: String,

  @JsonProperty("relation")
  val relation: String,

  @JsonProperty("subject")
  val subject: Subject,

  @JsonProperty("policy")
  val policy: String? = null
)
