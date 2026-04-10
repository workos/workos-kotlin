package com.workos.widgets.models

/**
 * The response object from creating a widget token.
 *
 * @param token The generated widget token.
 */
data class WidgetToken(
  @JvmField
  val token: String
)
