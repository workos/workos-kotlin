package com.workos.common.exceptions

class UnauthorizedException(
  override val message: String?,
  val requestId: String
) : Exception(message) {
  val status = 401
}
