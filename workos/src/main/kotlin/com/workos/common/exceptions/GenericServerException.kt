package com.workos.common.exceptions

class GenericServerException(val status: Int, val messsage: String?, val requestId: String) : Exception("GenericServerException")
