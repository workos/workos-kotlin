package com.workos.common.http

data class OAuthErrorResponse(
    val error: String?,
    val error_description: String?
)
