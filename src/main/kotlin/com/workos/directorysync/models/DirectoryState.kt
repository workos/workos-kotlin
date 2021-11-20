package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of states for a [Directory]
 *
 * @param state The Directory State string value.
 */
enum class DirectoryState(@JsonValue val state: String) {
  /**
   * The directory is currently being deleted.
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
  Unlinked("unlinked"),
}
