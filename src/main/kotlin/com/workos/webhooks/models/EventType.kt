package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of event types for a [WebhookEvent]
 *
 * @param event The Event Type string value.
 */
enum class EventType(@JsonValue val event: String) {
  /**
   * Triggers when a Connection's state changes to active.
   */
  ConnectionActivated("connection.activated"),

  /**
   * Triggers when a Connection's state changes to inactive.
   */
  ConnectionDeactivated("connection.deactivated"),

  /**
   * Triggers when a Connection is deleted.
   */
  ConnectionDeleted("connection.deleted"),

  /**
   * Triggers when a Directory's state changes to active.
   */
  DirectoryActivated("dsync.activated"),

  /**
   * Triggers when a Directory's state changes to inactive.
   */
  DirectoryDeactivated("dsync.deactivated"),

  /**
   * Triggers when a Directory is deleted.
   */
  DirectoryDeleted("dsync.deleted"),

  /**
   * Triggers when a user is created or added to the directory
   */
  DirectoryUserCreated("dsync.user.created"),

  /**
   * Triggers when a user's properties have been updated.
   */
  DirectoryUserUpdated("dsync.user.updated"),

  /**
   * Triggers when a user has been removed from a directory.
   */
  DirectoryUserDeleted("dsync.user.deleted"),

  /**
   * Triggers when a group is created or added to a directory.
   */
  DirectoryGroupCreated("dsync.group.created"),

  /**
   * Triggers when a group's properties have been updated.
   */
  DirectoryGroupUpdated("dsync.group.updated"),

  /**
   * Triggers when a user has been added to a group.
   */
  DirectoryGroupUserAdded("dsync.group.user_added"),

  /**
   * Triggers when a user has been removed from a group.
   */
  DirectoryGroupUserRemoved("dsync.group.user_removed"),

  /**
   * Triggers when a group is removed from a directory.
   */
  DirectoryGroupDeleted("dsync.group.deleted"),
}
