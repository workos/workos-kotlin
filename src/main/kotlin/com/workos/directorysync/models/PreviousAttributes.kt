package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object representing the previous attributes of names and values
 * that have changed and their previous values.
 *
 * @param previousAttributes The object containing the previous keys and values for changes.
 */
data class PreviousAttributes @JsonCreator constructor(
  @JvmField
  @JsonProperty("previous_attributes")
  val previousAttributes: Map<String, Any>
)
