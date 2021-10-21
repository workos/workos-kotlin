package com.workos

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkOSTest {
    @Test
    fun getShouldReturnSuccessStatusCode() {
        val workos = WorkOS("does an empty string count")

        val response = workos.get("/health")

        assertEquals(response.statusCode(), 200, "response code should be 200")
    }
}
