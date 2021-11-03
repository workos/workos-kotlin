package com.workos.common.exceptions

class OauthException(override val message: String?, val status: Int, val requestId: String, val error: String?, val errorDescription: String?) : Exception(message)
