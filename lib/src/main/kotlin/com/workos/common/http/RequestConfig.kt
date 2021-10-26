package com.workos.common.http

class RequestConfig(
    val headers: MutableMap<String, String>? = mutableMapOf(),
    val params: MutableMap<String, String>? = mutableMapOf()
)
