package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Describes the domain records associated with a [Connection]. This
 * class is not meant to be instantiated directly.
 *
 * @param domain The Domain for a connection record.
 * @param id Unique identifier for a Connection Domain.
 */
data class ConnectionDomain
@JsonCreator constructor(
  @JvmField
  val domain: String,

  @JvmField
  val id: String,
)
