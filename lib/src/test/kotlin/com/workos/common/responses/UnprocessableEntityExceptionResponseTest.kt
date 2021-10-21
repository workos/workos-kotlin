package com.workos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.common.responses.UnprocessableEntityExceptionResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class UnprocessableEntityExceptionResponseTest {
    private val mapper = jacksonObjectMapper()

    init {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    @Test
    fun responseShouldBeDeserialized() {
        val json = """
        {
            "errors": [{"field": "foo", "code": "1"}, {"field": "bar", "code": "2"}]
        }"""

        val response = mapper.readValue(json, UnprocessableEntityExceptionResponse::class.java)

        assertEquals("foo", response.errors.get(0).field)
        assertEquals("bar", response.errors.get(1).field)
    }
}
