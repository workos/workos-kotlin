package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  property = "code",
  visible = true,
  defaultImpl = Warning::class
)
@JsonSubTypes(
  JsonSubTypes.Type(value = MissingContextKeysWarning::class, name = "missing_context_keys")
)
open class Warning @JsonCreator constructor(
  @JsonProperty("code")
  open val code: String,

  @JsonProperty("message")
  open val message: String
)

@JsonTypeName("missing_context_keys")
data class MissingContextKeysWarning @JsonCreator constructor(
  @JsonProperty("code")
  override val code: String,

  @JsonProperty("message")
  override val message: String,

  @JsonProperty("keys")
  val keys: List<String>
) : Warning(code, message)
