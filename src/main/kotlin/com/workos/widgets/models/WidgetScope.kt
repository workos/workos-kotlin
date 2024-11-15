package com.workos.widgets.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * WidgetScope of the widget token.
 *
 * @param value The string value of the widgetscope.
 */
enum class WidgetScope(@JsonValue val value: String) {
  /**
   * Manage users via the users table widget.
   */
  UsersTableManagement("widgets:users-table:manage"),
}
