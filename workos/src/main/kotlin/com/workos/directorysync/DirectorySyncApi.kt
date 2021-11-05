package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.DirectoryGroupList
import com.workos.directorysync.models.DirectoryList
import com.workos.directorysync.models.DirectoryUserList
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import java.lang.IllegalArgumentException

/**
 * The DirectorySyncApi class provides convenience methods for working with the WorkOS
 * Directory Sync platform. You'll need a valid API key and to have created a Directory
 * Sync connection on your WorkOS dashboard.
 */
class DirectorySyncApi(private val workos: WorkOS) {
  /**
   * Deletes a single directory by the given [com.workos.directorysync.models.Directory] id.
   */
  fun deleteDirectory(id: String) {
    workos.delete("/directories/$id")
  }

  /**
   * Fetches the list of directories.
   */
  @JvmOverloads
  fun listDirectories(paginationParams: PaginationParams? = null): DirectoryList {
    val requestConfig = RequestConfig.builder()
      .params(paginationParams ?: emptyMap())
      .build()

    return workos.get(
      "/directories", DirectoryList::class.java, requestConfig
    )
  }

  /**
   * Fetches a directory user by id.
   */
  fun getDirectoryUser(id: String): User {
    return workos.get(
      "/directory_users/$id", User::class.java
    )
  }

  /**
   * Fetches the list of users based on the given options.
   */
  fun listDirectoryUsers(
    listOptions: ListDirectoryUserOptions
  ): DirectoryUserList {
    if (listOptions["directory"] == null && listOptions["group"] == null) {
      throw IllegalArgumentException("A directory or group must be provided.")
    }

    val requestConfig = RequestConfig.builder()
      .params(listOptions)
      .build()

    return workos.get(
      "/directory_users", DirectoryUserList::class.java, requestConfig
    )
  }

  /**
   * Fetches a directory group by id.
   */
  fun getDirectoryGroup(id: String): Group {
    return workos.get(
      "/directory_groups/$id", Group::class.java
    )
  }

  /**
   * Fetches the list of directory groups.
   */
  fun listDirectoryGroups(
    listOptions: ListDirectoryGroupOptions
  ): DirectoryGroupList {
    if (listOptions["directory"] == null && listOptions["user"] == null) {
      throw IllegalArgumentException("Either directory or user must be provided.")
    }

    val requestConfig = RequestConfig.builder()
      .params(listOptions)
      .build()

    return workos.get(
      "/directory_groups", DirectoryGroupList::class.java, requestConfig
    )
  }

  /**
   * Parameters for [listDirectoryGroups]
   *
   * @param directory the id of the directory to list groups for
   * @param user the id of the user to list groups for
   * @param after @see [com.workos.common.http.PaginationParams]
   */
  class ListDirectoryGroupOptions @JvmOverloads constructor(
    directory: String? = null,
    user: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
  ) : PaginationParams(after, before, limit) {
    init {
      if (directory != null) set("directory", directory)
      if (user != null) set("user", user)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListDirectoryGroupOptionsBuilder {
        return ListDirectoryGroupOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [ListDirectoryGroupOptions]
     */
    class ListDirectoryGroupOptionsBuilder : PaginationParams.Builder<ListDirectoryGroupOptions>(ListDirectoryGroupOptions()) {
      /**
       * The directory id to filter on.
       */
      fun directory(value: String) = apply { this.params["directory"] = value }
      /**
       * The user id to filter on.
       */
      fun user(value: String) = apply { this.params["user"] = value }
    }
  }

  /**
   * Parameters for [listDirectoryUsers]
   *
   * @param directory The ID of the directory to list the user for.
   * @param group the id of the gruop to list users for
   * @param after @see [com.workos.common.http.PaginationParams]
   * @param before @see [com.workos.common.http.PaginationParams]
   * @param limit @see [com.workos.common.http.PaginationParams]
   */
  class ListDirectoryUserOptions @JvmOverloads constructor(
    directory: String? = null,
    group: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
  ) : PaginationParams(after, before, limit) {
    init {
      if (directory != null) set("directory", directory)
      if (group != null) set("group", group)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListDirectoryUserOptionsBuilder {
        return ListDirectoryUserOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [ListDirectoryUserOptions]
     */
    class ListDirectoryUserOptionsBuilder : PaginationParams.Builder<ListDirectoryUserOptions>(ListDirectoryUserOptions()) {
      /**
       * The directory id to filter on.
       */
      fun directory(value: String) = apply { this.params["directory"] = value }
      /**
       * The group id to filter on.
       */
      fun group(value: String) = apply { this.params["group"] = value }
    }
  }
}
