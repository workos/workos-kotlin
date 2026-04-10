package com.workos.portal.models

/**
 * The response object from creating a link.
 *
 * @param link The generated URL for access to the Admin Portal.
 */
data class Link(
  @JvmField
  val link: String
)
