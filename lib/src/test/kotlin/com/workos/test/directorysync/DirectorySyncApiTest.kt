package com.workos.test.directorysync

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.http.PaginationParams
import com.workos.directorysync.DirectorySyncApi
import com.workos.directorysync.models.DirectoryState
import com.workos.directorysync.models.DirectoryType
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectorySyncApiTest : TestBase() {

  @Test
  fun listDirectoriesWithNoParamsShouldReturnDirectories() {
    val workos = createWorkOSClient()

    val gsuiteDirectoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val oktaDirectoryId = "directory_01E8CS3GSBEBZ1F1CZAEE3KHDG"

    stubResponse(
      url = "/directories",
      responseBody = """{
        "data": [{
          "id": "$gsuiteDirectoryId",
          "domain": "foo-corp.com",
          "name": "Foo Corp",
          "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
          "object": "directory",
          "state": "unlinked",
          "type": "gsuite directory",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z"
        },
        {
          "id": "$oktaDirectoryId",
          "domain": "foo-corp.com",
          "external_key": "r3NDlInUnAe6i4wG",
          "name": "Foo Corp",
          "organization_id": "org_01EHZNVPK3SFK441A1RGBFPANT",
          "object": "directory",
          "state": "linked",
          "type": "okta scim v2.0",
          "created_at": "2021-06-25T19:09:33.155Z",
          "updated_at": "2021-06-25T19:10:33.155Z"
        }],
        "listMetadata" : {
          "after" : "someAfterId",
          "before" : "someBeforeId"
        }
      }""",
    )

    val (data) = workos.directorySync.listDirectories()

    val gsuiteDirectory = data[0]
    val oktaDirectory = data[1]

    assertEquals(gsuiteDirectory.id, gsuiteDirectoryId)
    assertEquals(oktaDirectory.id, oktaDirectoryId)
    assertEquals(gsuiteDirectory.type, DirectoryType.GSuiteDirectory)
    assertEquals(oktaDirectory.type, DirectoryType.OktaSCIMV2_0)
  }

  @Test
  fun listDirectoriesWithParamsShouldReturnDirectories() {
    val workos = createWorkOSClient()

    val gsuiteDirectoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directories",
      params = mapOf(
        "after" to equalTo("someAfterId"),
        "before" to equalTo("someBeforeId"),
        "limit" to equalTo("1")
      ),
      responseBody = """{
        "data": [{
          "id": "$gsuiteDirectoryId",
          "domain": "foo-corp.com",
          "name": "Foo Corp",
          "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
          "object": "directory",
          "state": "unlinked",
          "type": "gsuite directory",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z"
        }],
        "listMetadata" : {
          "after" : "someAfterId",
          "before" : "someBeforeId"
        }
      }""",
      responseStatus = 200,
    )

    val paginationParams = PaginationParams.builder()
      .after("someAfterId")
      .before("someBeforeId")
      .limit((1))
      .build()

    val (data) = workos.directorySync.listDirectories(paginationParams)

    val gsuiteDirectory = data[0]

    assertEquals(gsuiteDirectory.id, gsuiteDirectoryId)
    assertEquals(gsuiteDirectory.type, DirectoryType.GSuiteDirectory)
    assertEquals(gsuiteDirectory.state, DirectoryState.Unlinked)
  }

  @Test
  fun getDirectoryGroupShouldReturnDirectoryGroup() {
    val workos = createWorkOSClient()

    val directoryId = "directory_group_01E64QTDNS0EGJ0FMCVY9BWGZT"

    stubResponse(
      url = "/directory_groups/$directoryId",
      responseBody = """{
        "id" : "$directoryId",
        "name" : "Developers"
      }"""
    )

    val response = workos.directorySync.getDirectoryGroup(directoryId)
    assertEquals(response.id, directoryId)
  }

  @Test
  fun listDirectoryGroupsWithNoParamsShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"

    stubResponse(
      url = "/directory_groups",
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "name" : "Developers"
        }],
        "listMetadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
    )

    val (data) = workos.directorySync.listDirectoryGroups()

    val directoryGroup = data[0]

    assertEquals(directoryGroup.id, directoryGroupId)
  }

  @Test
  fun listDirectoryGroupsWithPaginationParamsShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_groups",
      params = mapOf(
        "after" to equalTo("someAfterId"),
        "before" to equalTo("someBeforeId"),
        "limit" to equalTo("1")
      ),
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "name" : "Developers"
        }],
        "listMetadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryGroupOptions.builder()
      .after("someAfterId")
      .before("someBeforeId")
      .limit((1))
      .build()

    val (data) = workos.directorySync.listDirectoryGroups(listOptions)

    val directoryGroup = data[0]

    assertEquals(directoryGroup.id, directoryGroupId)
  }

  @Test
  fun listDirectoryGroupsWithOtherParamsShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val directoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val userId = "directory_user_01E1JG7J09H96KYP8HM9B0G5SJ"

    stubResponse(
      url = "/directory_groups",
      params = mapOf(
        "directory" to equalTo(directoryId),
        "user" to equalTo(userId),
      ),
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "name" : "Developers"
        }],
        "listMetadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryGroupOptions.builder()
      .directory(directoryId)
      .user(userId)
      .build()

    val (data) = workos.directorySync.listDirectoryGroups(listOptions)

    val directoryGroup = data[0]

    assertEquals(directoryGroup.id, directoryGroupId)
  }

  @Test
  fun listDirectoryGroupsWithRawParamsShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val directoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val userId = "directory_user_01E1JG7J09H96KYP8HM9B0G5SJ"

    stubResponse(
      url = "/directory_groups",
      params = mapOf(
        "directory" to equalTo(directoryId),
        "user" to equalTo(userId),
        "after" to equalTo("after"),
        "before" to equalTo("before"),
        "limit" to equalTo("10")
      ),
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "name" : "Developers"
        }],
        "listMetadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryGroupOptions(
      directory = directoryId,
      user = userId,
      after = "after",
      before = "before",
      limit = 10
    )

    val (data) = workos.directorySync.listDirectoryGroups(listOptions)

    val directoryGroup = data[0]

    assertEquals(directoryGroup.id, directoryGroupId)
  }
}
