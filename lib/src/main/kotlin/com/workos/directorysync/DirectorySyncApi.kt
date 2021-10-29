package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.DirectoryGroupList
import com.workos.directorysync.models.DirectoryList
import com.workos.directorysync.models.DirectoryUserList
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import java.security.InvalidParameterException

class DirectorySyncApi(private val workos: WorkOS) {

  fun deleteDirectory(id: String) {
    workos.delete("/directory/$id")
  }

  @JvmOverloads
  fun listDirectories(paginationParams: PaginationParams? = null): DirectoryList {
    val params = paginationParams ?: emptyMap()
    val requestConfig = RequestConfig.builder()
      .params(params)
      .build()

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
    listOptions: ListDirectoryUserOptions
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
    listOptions: ListDirectoryGroupOptions
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
      validateParams(this)
    }

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }

      fun validateParams(params: Map<String, String>): Boolean {
        if (params["directory"] == null && params["user"] == null) {
          throw InvalidParameterException("Either directory or user must be provided.")
        }
        return true
      }
    }

    class Builder : PaginationParams.Builder<ListDirectoryGroupOptions>(
      ListDirectoryGroupOptions(directory = "")
    ) {
      init { this.params.remove("directory") }
      fun directory(value: String) = apply { this.params["directory"] = value }
      fun user(value: String) = apply { this.params["user"] = value }

      override fun validateBuilderParams(): Boolean {
        return validateParams(this.params)
      }
    }
  }

  class ListDirectoryUserOptions @JvmOverloads constructor(
    directory: String? = null,
    group: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (directory != null) set("directory", directory)
      if (group != null) set("group", group)
      validateParams(this)
    }

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }

      fun validateParams(params: Map<String, String>): Boolean {
        if (params["directory"] == null && params["group"] == null) {
          throw InvalidParameterException("A directory or group must be provided.")
        }
        return true
      }
    }

    class Builder : PaginationParams.Builder<ListDirectoryUserOptions>(
      ListDirectoryUserOptions(directory = "")
    ) {
      init { this.params.remove("directory") }
      fun directory(value: String) = apply { this.params["directory"] = value }
      fun group(value: String) = apply { this.params["group"] = value }

      override fun validateBuilderParams(): Boolean {
        return validateParams(this.params)
      }
    }
  }
}
