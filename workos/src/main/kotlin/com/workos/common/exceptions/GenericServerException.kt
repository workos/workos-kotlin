package com.workos.common.exceptions

class GenericServerException(val status: Int, override val message: String?, val requestId: String) : Exception(message)
