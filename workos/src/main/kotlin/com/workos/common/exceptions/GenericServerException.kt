package com.workos.common.exceptions

class GenericServerException(override val message: String?, val status: Int, val requestId: String) : Exception(message)
