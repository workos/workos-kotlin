package com.workos.widgets.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.widgets.models.WidgetScope

class GetTokenOptions(
  /**
   * The ID of the organization to generate a token for.
   */
  @JsonProperty("organization_id")
  val organizationId: String,

  /**
   * The ID of the user to generate a token for.
   */
  @JsonProperty("user_id")
  val userId: String,

  /**
   * The scopes to generate a token for.
   */
  @JsonProperty("scopes")
  val scopes: List<WidgetScope>,
) {
  init {
    require(organizationId.isNotBlank()) { "Organization ID is required" }
    require(userId.isNotBlank()) { "User ID is required" }
    require(scopes.isNotEmpty()) { "At least one scope is required" }
  }
}
