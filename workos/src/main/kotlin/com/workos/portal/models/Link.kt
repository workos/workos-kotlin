package com.workos.portal.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * The response object from creating a link.
 *
 * @param link The generated URL for access to the Admin Portal.
 */
data class Link
@JsonCreator constructor(
  @JvmField
  val link: String
)
