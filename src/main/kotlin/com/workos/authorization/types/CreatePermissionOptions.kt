package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreatePermissionOptions
  @JvmOverloads
  constructor(
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("resource_type_slug")
    val resourceTypeSlug: String? = null
  )
