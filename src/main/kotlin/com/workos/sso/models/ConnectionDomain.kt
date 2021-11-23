package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Describes the domain records associated with a [Connection]. This
 * class is not meant to be instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param domain The Domain for a connection record.
 * @param id Unique identifier for a Connection Domain.
 */
data class ConnectionDomain
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "connection_domain",

  @JvmField
  val domain: String,

  @JvmField
  val id: String,
)
