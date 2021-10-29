package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.DirectoryGroupList
import com.workos.directorysync.models.DirectoryList
import com.workos.directorysync.models.DirectoryUserList
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User

class DirectorySyncApi(private val workos: WorkOS) {

  fun deleteDirectory(id: String) {
    workos.delete("/directory/$id")
  }

  fun listDirectories(paginationParams: PaginationParams? = PaginationParams()): DirectoryList {
    val requestConfig = RequestConfig(params = paginationParams)
    return workos.get(
      "/directories", DirectoryList::class.java, requestConfig
    )
  }

  fun getDirectoryUser(id: String): User {
    return workos.get(
      "/directory_user/$id", User::class.java
    )
  }

  fun listDirectoryUsers(
    listOptions: ListDirectoryUserOptions = ListDirectoryUserOptions()
  ): DirectoryUserList {
    val requestConfig = RequestConfig.builder()
      .params(listOptions)
      .build()

    return workos.get(
      "/directory_users", DirectoryUserList::class.java, requestConfig
    )
  }

  fun getDirectoryGroup(id: String): Group {
    return workos.get(
      "/directory_groups/$id", Group::class.java
    )
  }

  fun listDirectoryGroups(
    listOptions: ListDirectoryGroupOptions = ListDirectoryGroupOptions()
  ): DirectoryGroupList {
    val requestConfig = RequestConfig.builder()
      .params(listOptions)
      .build()

    return workos.get(
      "/directory_groups", DirectoryGroupList::class.java, requestConfig
    )
  }

  class ListDirectoryGroupOptions @JvmOverloads constructor(
    directory: String? = null,
    user: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (directory != null) set("directory", directory)
      if (user != null) set("user", user)
    }

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder : PaginationParams.Builder<ListDirectoryGroupOptions>(ListDirectoryGroupOptions()) {
      fun directory(value: String) = apply { this.params["directory"] = value }
      fun user(value: String) = apply { this.params["user"] = value }
    }
  }

  class ListDirectoryUserOptions @JvmOverloads constructor(
    group: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (group != null) set("group", group)
    }

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder : PaginationParams.Builder<ListDirectoryUserOptions>(ListDirectoryUserOptions()) {
      fun group(value: String) = apply { this.params["group"] = value }
    }
  }
}
