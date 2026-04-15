package com.workos.directorysync.models

/**
 * An email for a Directory [User]. This class is not meant to be
 * instantiated directly.
 *
 * @param primary Whether or not it is the primary email.
 * @param type The type of email e.g. 'work', 'personal'.
 * @param value The email address.
 */
data class Email(
  @JvmField
  val primary: Boolean?,
  @JvmField
  val type: String?,
  @JvmField
  val value: String?
)
