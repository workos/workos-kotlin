package com.workos.common.exceptions

class UnauthorizedException(val requestId: String) : Exception("UnauthorizedException") {
  val status = 401
}
