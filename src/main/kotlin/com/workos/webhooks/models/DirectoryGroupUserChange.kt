package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User

/**
 * Represents a WorkOS Directory resource. This class is not meant to be
 * instantiated directly.
 *
 * @param id The unique identifier for the Directory.
 * @param group The modified [com.workos.directorysync.models.Group].
 * @param user The modified [com.workos.directorysync.models.User].
 */
data class DirectoryGroupUserChange
@JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  val user: User,

  @JvmField
  val group: Group,
)
