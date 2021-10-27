package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.DirectoryList
import com.workos.directorysync.models.Group

class DirectorySyncApi(private val workos: WorkOS) {
    fun listDirectories(paginationParams: PaginationParams? = PaginationParams()): DirectoryList {
        val requestConfig = RequestConfig(params = paginationParams)
        return workos.get(
            "/directories", DirectoryList::class.java, requestConfig
        )
    }

    fun getDirectoryGroup(id: String): Group {
        return workos.get(
            "/directory_groups/$id", Group::class.java
        )
    }
}
