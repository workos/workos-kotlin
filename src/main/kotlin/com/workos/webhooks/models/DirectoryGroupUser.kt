package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User

/**
 * Represents the Webhook payload for a user change within a WorkOS Directory Group.
 * This class is not meant to be instantiated directly.
 *
 * @param directoryId The unique identifier for the Directory.
 * @param group The modified [com.workos.directorysync.models.Group].
 * @param user The modified [com.workos.directorysync.models.User].
 */
data class DirectoryGroupUser
@JsonCreator constructor(
  @JvmField
  @JsonProperty("directory_id")
  val directoryId: String,

  @JvmField
  val user: User,

  @JvmField
  val group: Group,
)
