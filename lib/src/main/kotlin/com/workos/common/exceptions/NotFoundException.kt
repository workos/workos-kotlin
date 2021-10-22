package com.workos.common.exceptions

class NotFoundException(val path: String, val requestId: String) : Exception("NotFoundException") {
    val status = 404
}
