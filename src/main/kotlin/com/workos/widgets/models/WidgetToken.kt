package com.workos.widgets.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * The response object from creating a widget token.
 *
 * @param token The generated widget token.
 */
data class WidgetToken
@JsonCreator constructor(
  @JvmField
  val token: String
)
