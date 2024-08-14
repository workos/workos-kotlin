package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.fga.models.Subject

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateWarrantOptions @JvmOverloads constructor(
  /**
   * The type of the resource.
   */
  @JsonProperty("resource_type")
  resourceType: String,

  /**
   * The unique ID of the resource.
   */
  @JsonProperty("resource_id")
  resourceId: String,

  /**
   * The relation for this resource to subject association. The relation must be valid per the resource type definition.
   */
  @JsonProperty("relation")
  relation: String,

  /**
   * The resource with the specified `relation` to the resource provided by resourceType and resourceId.
   */
  @JsonProperty("subject")
  subject: Subject,

  /**
   * A boolean expression that must evaluate to true for this warrant to apply. The expression can reference variables provided in the context attribute of access check requests.
   */
  @JsonProperty("policy")
  policy: String? = null,
) : WriteWarrantOptions("create", resourceType, resourceId, relation, subject, policy)
