package com.workos.common.exceptions

class OauthException(val status: Int, val requestId: String, val error: String?, val errorDescription: String?) : Exception("OauthException")
