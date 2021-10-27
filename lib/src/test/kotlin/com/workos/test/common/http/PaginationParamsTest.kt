package com.workos.test.common.http

import com.workos.common.http.PaginationParams
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationParamsTest {

    @Test
    fun paginationParamsBuilderShouldBuildAMap() {
        var params = PaginationParams.Builder()
            .after("after")
            .before("before")
            .limit(10)
            .build()

        assertTrue(params is MutableMap<String, String>)
    }

    @Test
    fun paginationParamsBuilderShouldBuildTheExpectedParams() {
        var params = PaginationParams.Builder()
            .after("after")
            .before("before")
            .limit(10)
            .build()

        assertEquals(params["after"], "after")
        assertEquals(params["before"], "before")
        assertEquals(params["limit"], "10")
    }
}
