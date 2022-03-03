package com.workos.directorysync

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.directorysync.models.* // ktlint-disable no-wildcard-imports
import java.lang.IllegalArgumentException

/**
 * The DirectorySyncApi class provides convenience methods for working with the WorkOS
 * Directory Sync platform. You'll need a valid API key and to have created a Directory
 * Sync connection on your WorkOS dashboard.
 */
class DirectorySyncApi(private val workos: WorkOS) {
  /**
   * Deletes a single directory by the given [com.workos.directorysync.models.Directory] ID.
   */
  fun deleteDirectory(id: String) {
    workos.delete("/directories/$id")
  }

  /**
   * Fetches a single directory by the given [com.workos.directorysync.models.Directory] ID.
   */
  fun getDirectory(id: String): Directory {
    return workos.get(
      "/directories/$id", Directory::class.java
    )
  }

  /**
   * Fetches the list of directories.
   */
  @JvmOverloads
  fun listDirectories(listOptions: ListDirectoriesOptions? = null): DirectoryList {

    val requestConfig = RequestConfig.builder()
      .params(listOptions ?: emptyMap())
      .build()

    return workos.get(
      "/directories", DirectoryList::class.java, requestConfig
    )
  }

  /**
   * Parameters for [listDirectories]
   *
   * @param organization The identifier of the organization to list directories for.
   * @param after @see [com.workos.common.http.PaginationParams].
   * @param before @see [com.workos.common.http.PaginationParams]
   * @param limit @see [com.workos.common.http.PaginationParams]
   */
  class ListDirectoriesOptions @JvmOverloads constructor(
    organization: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
  ) : PaginationParams(after, before, limit) {
    init {
      if (organization != null) set("organization_id", organization)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListDirectoriesOptionsBuilder {
        return ListDirectoriesOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [ListDirectoriesOptions].
     */
    class ListDirectoriesOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListDirectoriesOptions>(ListDirectoriesOptions()) {
      /**
       * The organization identifier to filter on.
       */
      fun organization(value: String) = apply { this.params["organization_id"] = value }
    }
  }

  /**
   * Builder class for creating [ListDirectoryGroupOptions].
   */
  class ListDirectoryGroupOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListDirectoryGroupOptions>(ListDirectoryGroupOptions()) {
    /**
     * The directory identifier to filter on.
     */
    fun directory(value: String) = apply { this.params["directory"] = value }
    /**
     * The user identifier to filter on.
     */
    fun user(value: String) = apply { this.params["user"] = value }
  }

  /**
   * Fetches a directory user by the given [com.workos.directorysync.models.User] ID.
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
   * Fetches a directory group by the given [com.workos.directorysync.models.Group] ID.
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
   * @param directory The identifier of the directory to list groups for.
   * @param user The identifier of the user to list groups for.
   * @param after @see [com.workos.common.http.PaginationParams].
   * @param before @see [com.workos.common.http.PaginationParams]
   * @param limit @see [com.workos.common.http.PaginationParams]
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
     * Builder class for creating [ListDirectoryGroupOptions].
     */
    class ListDirectoryGroupOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListDirectoryGroupOptions>(ListDirectoryGroupOptions()) {
      /**
       * The directory identifier to filter on.
       */
      fun directory(value: String) = apply { this.params["directory"] = value }
      /**
       * The user identifier to filter on.
       */
      fun user(value: String) = apply { this.params["user"] = value }
    }
  }

  /**
   * Parameters for [listDirectoryUsers].
   *
   * @param directory The ID of the directory to list the user for.
   * @param group The ID of the group to list users for.
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
     * Builder class for creating [ListDirectoryUserOptions].
     */
    class ListDirectoryUserOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListDirectoryUserOptions>(ListDirectoryUserOptions()) {
      /**
       * The directory identifier to filter on.
       */
      fun directory(value: String) = apply { this.params["directory"] = value }
      /**
       * The group identifier to filter on.
       */
      fun group(value: String) = apply { this.params["group"] = value }
    }
  }
}
