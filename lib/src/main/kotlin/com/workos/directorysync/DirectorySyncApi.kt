package com.workos.directorysync

import com.workos.WorkOS
import com.workos.directorysync.models.Directory

class DirectorySyncApi(private val client: WorkOS) {
    fun listDirectories(): Directory {
        return this.client.get<Directory>(path = "/directories", responseType = Directory::class.java);
    }
}