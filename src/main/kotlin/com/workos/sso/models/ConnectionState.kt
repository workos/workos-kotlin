package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of states for a [Connection]
 *
 * @param state The Connection State string value.
 */
enum class ConnectionState(@JsonValue val state: String) {
  /**
   * The connection is active and able to authenticate users.
   */
  Active("active"),
  /**
   * The connection has been deleted or is in a deleting status.
   */
  Draft("deleting"),

  /**
   * The connection has been created and requires configuration to be activated.
   */
  Draft("draft"),

  /**
   * The connection is inactive and unable to authenticate users.
   */
  Inactive("inactive"),

  /**
   * The connection has been created and requires validation to be activated.
   */
  Validating("validating"),
}
