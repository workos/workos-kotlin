package com.workos

import com.workos.common.exceptions.NotFoundException
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertNotNull

class WorkOSTest {
    class ExampleResponseType {
        val message: String = ""
    }

    @Test
    fun getShouldReturnSuccessStatusCode() {
        val workos = WorkOS("apiKey")

        val response = workos.get<ExampleResponseType>("/", ExampleResponseType::class.java)

        assertNotNull("message should be defined", response.message)
    }

    @Test
    fun getShouldReturnNotFound() {
        val workos = WorkOS("apiKey")

        assertThrows(NotFoundException::class.java) {
            workos.get<ExampleResponseType>("/not-real", ExampleResponseType::class.java)
        }
    }

    @Test
    fun postShouldReturnNotFound() {
        val workos = WorkOS("apiKey")

        assertThrows(NotFoundException::class.java) {
            workos.post<ExampleResponseType>("/not-real", ExampleResponseType::class.java)
        }
    }

    @Test
    fun putShouldReturnNotFound() {
        val workos = WorkOS("apiKey")

        assertThrows(NotFoundException::class.java) {
            workos.put<ExampleResponseType>("/not-real", ExampleResponseType::class.java)
        }
    }

    @Test
    fun deleteShouldReturnNotFound() {
        val workos = WorkOS("apiKey")

        assertThrows(NotFoundException::class.java) {
            workos.delete("/not-real")
        }
    }
}
