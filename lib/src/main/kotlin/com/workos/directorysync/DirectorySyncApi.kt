package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.DirectoryList

class DirectorySyncApi(private val client: WorkOS) {
    fun listDirectories(paginationParams: PaginationParams? = PaginationParams.emptyParams()): DirectoryList {
        val requestConfig = RequestConfig(params = paginationParams)
        return this.client.get(
            path = "/directories", responseType = DirectoryList::class.java, requestConfig
        )
    }
}
