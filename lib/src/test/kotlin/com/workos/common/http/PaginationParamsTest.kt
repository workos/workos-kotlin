package com.workos.common.http

import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationParamsTest {

    @Test
    fun PaginationParamsBuilderShouldBuildAMap() {
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
