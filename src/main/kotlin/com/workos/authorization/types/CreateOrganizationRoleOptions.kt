package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateOrganizationRoleOptions
  @JvmOverloads
  constructor(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String? = null,
    @JsonProperty("description")
    val description: String? = null
  )
