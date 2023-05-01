package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of states for a [Directory]
 *
 * @param state The Directory State string value.
 */
enum class DirectoryState(@JsonValue val state: String) {
  /**
   * The directory is connected to an external provider.
   * This state exists only from the webhook payload
   * and correlates to 'Unlinked' which we plan to deprecate later.
   */
  Active("active"),

  /**
   * The directory is currently being validated.
   */
  Validating("validating"),

  /**
   * The directory is no longer connected to an external provider.
   * This state exists only from the webhook payload
   * and correlates to 'Unlinked' which we plan to deprecate later.
   */
  Inactive("inactive"),

  /**
   * The final state before a directory is deleted.
   */
  Deleting("deleting"),

  /**
   * The directory is unable to sync due to invalid credentials.
   */
  InvalidCredentials("invalid_credentials"),

  /**
   * The directory is successfully linked and will sync.
   */
  Linked("linked"),

  /**
   * The directory is not linked and will not sync.
   */
  Unlinked("unlinked")
}
