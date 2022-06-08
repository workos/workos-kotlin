package com.workos.test.directorysync

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.exceptions.UnauthorizedException
import com.workos.directorysync.DirectorySyncApi
import com.workos.directorysync.models.DirectoryState
import com.workos.directorysync.models.DirectoryType
import com.workos.directorysync.models.UserState
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectorySyncApiTest : TestBase() {

  @Test
  fun deleteDirectoryShouldNotError() {
    val workos = createWorkOSClient()

    val id = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directories/$id",
      responseBody = "{}"
    )

    val response = workos.directorySync.deleteDirectory(id)

    assertEquals(Unit, response)
  }

  @Test
  fun deleteDirectoryShouldThrowError() {
    val workos = createWorkOSClient()

    val id = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directories/$id",
      responseBody = "{}",
      responseStatus = 401
    )

    assertThrows(UnauthorizedException::class.java) {
      workos.directorySync.deleteDirectory(id)
    }
  }

  @Test
  fun getDirectoryShouldReturnDirectory() {
    val workos = createWorkOSClient()

    val gsuiteDirectoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directories/$gsuiteDirectoryId",
      responseBody = """{
          "id": "$gsuiteDirectoryId",
          "idp_id": "02grqrue4294w24",
          "domain": "foo-corp.com",
          "external_key": "abcdefghi",
          "name": "Foo Corp",
          "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
          "object": "directory",
          "state": "unlinked",
          "type": "gsuite directory",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z"
        }"""
    )

    val response = workos.directorySync.getDirectory(gsuiteDirectoryId)
    assertEquals(response.id, gsuiteDirectoryId)
    assertEquals(response.state, DirectoryState.Unlinked)
    assertEquals(response.type, DirectoryType.GSuiteDirectory)
  }

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
          "idp_id": "02grqrue4294w24",
          "domain": "foo-corp.com",
          "external_key": "abcdefghi",
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
        "list_metadata" : {
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
          "idp_id": "02grqrue4294w24",
          "domain": "foo-corp.com",
          "external_key": "abcdefgh",
          "name": "Foo Corp",
          "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
          "object": "directory",
          "state": "unlinked",
          "type": "gsuite directory",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z"
        }],
        "list_metadata" : {
          "after" : "someAfterId",
          "before" : "someBeforeId"
        }
      }""",
      responseStatus = 200,
    )

    val paginationParams = DirectorySyncApi.ListDirectoriesOptions.builder()
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
  fun listDirectoriesWithOrganizationParamShouldReturnDirectories() {
    val workos = createWorkOSClient()

    val gsuiteOrganizationId = "org_01EHZNVPK3SFK441A1RGBFSHRT"

    stubResponse(
      url = "/directories",

      responseBody = """{
        "data": [{
          "id": "directory_01EHZNVPK3SFK441AXXXXXSHRT",
          "idp_id": "02grqrue4294w24",
          "domain": "foo-corp.com",
          "external_key": "abcdefgh",
          "name": "Foo Corp",
          "organization_id": "$gsuiteOrganizationId",
          "object": "directory",
          "state": "unlinked",
          "type": "gsuite directory",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z"
        }],
        "list_metadata" : {
          "after" : "someAfterId",
          "before" : "someBeforeId"
        }
      }""",
      responseStatus = 200,
    )

    val listDirectoriesOptions = DirectorySyncApi.ListDirectoriesOptions.builder()
      .organization(gsuiteOrganizationId)
      .after("someAfterId")
      .before("someBeforeId")
      .limit((1))
      .build()

    val (data) = workos.directorySync.listDirectories(listDirectoriesOptions)

    val gsuiteDirectory = data[0]

    assertEquals(gsuiteDirectory.organizationId, gsuiteOrganizationId)
    assertEquals(gsuiteDirectory.type, DirectoryType.GSuiteDirectory)
    assertEquals(gsuiteDirectory.state, DirectoryState.Unlinked)
  }

  @Test
  fun getDirectoryUserShouldReturnDirectoryUser() {
    val workos = createWorkOSClient()

    val userId = "directory_user_01E1JG7J09H96KYP8HM9B0G5SJ"
    val gsuiteDirectoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_users/$userId",
      responseBody = """{
        "id": "$userId",
        "idp_id": "2836",
        "directory_id": "$gsuiteDirectoryId",
        "emails": [{
          "primary": true,
          "type": "work",
          "value": "marcelina@foo-corp.com"
        }],
        "first_name": "Marcelina",
        "last_name": "Davis",
        "username": "marcelina@foo-corp.com",
        "groups": [{
          "id": "directory_group_01E64QTDNS0EGJ0FMCVY9BWGZT",
          "idp_id": "02grqrue4294w24",
          "directory_id": "$gsuiteDirectoryId",
          "name": "Engineering",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z",
          "raw_attributes": {}
        }],
        "state": "active",
        "custom_attributes": {
          "department": "Engineering"
        },
        "raw_attributes": {}
      }"""
    )

    val response = workos.directorySync.getDirectoryUser(userId)
    assertEquals(response.id, userId)
    assertEquals(response.state, UserState.Active)
    assertEquals(response.customAttributes["department"], "Engineering")
  }

  @Test
  fun getDirectoryGroupShouldReturnDirectoryGroup() {
    val workos = createWorkOSClient()

    val groupId = "directory_group_01E64QTDNS0EGJ0FMCVY9BWGZT"
    val directoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_groups/$groupId",
      responseBody = """{
        "object": "directory_group",
        "directory_id": "$directoryId",
        "id" : "$groupId",
        "idp_id": "02grqrue4294w24",
        "name" : "Developers",
        "raw_attributes": {}
      }"""
    )

    val response = workos.directorySync.getDirectoryGroup(groupId)
    assertEquals(response.id, groupId)
  }

  @Test
  fun listDirectoryGroupsWithUserParamShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val userId = "directory_user_01E1JG7J09H96KYP8HM9B0G5SJ"

    stubResponse(
      url = "/directory_groups",
      params = mapOf(
        "user" to equalTo(userId),
      ),
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "idp_id": "02grqrue4294w24",
          "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
          "name" : "Developers",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z",
          "raw_attributes": {}
        }],
        "list_metadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryGroupOptions.builder()
      .user(userId)
      .build()

    val (data) = workos.directorySync.listDirectoryGroups(listOptions)

    val directoryGroup = data[0]

    assertEquals(directoryGroup.id, directoryGroupId)
  }

  @Test
  fun listDirectoryGroupsWithDirectoryParamShouldReturnDirectoryGroups() {
    val workos = createWorkOSClient()

    val directoryGroupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"
    val directoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_groups",
      params = mapOf(
        "directory" to equalTo(directoryId),
      ),
      responseBody = """{
        "data" : [{
          "id" : "$directoryGroupId",
          "idp_id": "02grqrue4294w24",
          "directory_id": "$directoryId",
          "name" : "Developers",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z",
          "raw_attributes": {}
        }],
        "list_metadata" : {
          "after" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z",
          "before" : "directory_group_01E1JJS84MFPPQ3G655FHTKX6Z"
        }
      }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryGroupOptions.builder()
      .directory(directoryId)
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
          "idp_id": "02grqrue4294w24",
          "directory_id": "$directoryId",
          "name" : "Developers",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z",
          "raw_attributes": {}
        }],
        "list_metadata" : {
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

  @Test
  fun listDirectoryUsersWithDirectoryParamShouldReturnDirectoryUsers() {
    val workos = createWorkOSClient()

    val userId1 = "directory_user_01E1JJHG3BFJ3FNRRHSFWEBNCS"
    val userId2 = "directory_user_01E1JJHG10ANRA2V6PAX3GD7TE"
    val groupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_users",
      params = mapOf(
        "directory" to equalTo("directoryId"),
      ),
      responseBody = """{
          "data": [{
            "id": "$userId1",
            "idp_id": "1902",
            "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "jan@foo-corp.com"
            }],
            "first_name": "Jan",
            "last_name": "Brown",
            "username": "jan@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
                "name": "Engineering",
                "created_at": "2021-06-25T19:07:33.155Z",
                "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
           },
           {
            "id": "$userId2",
            "idp_id": "8953",
            "directory_id": "directoryId",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "rosalinda@foo-corp.com"
            }],
            "first_name": "Rosalinda",
            "last_name": "Swift",
            "username": "rosalinda@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Engineering",
              "created_at": "2021-06-25T19:07:33.155Z",
              "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
          }],
          "object": "list",
          "list_metadata": {
            "after": "directory_user_01E4RH82CC8QAP8JTRCTNDSS4C",
            "before": "directory_user_01E4RH828021B9ZZB8KH8E2Z1W"
          }
        }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryUserOptions
      .builder()
      .directory("directoryId")
      .build()

    val (data) = workos.directorySync.listDirectoryUsers(listOptions)

    val directoryUser1 = data[0]
    val directoryUser2 = data[1]

    assertEquals(directoryUser1.id, userId1)
    assertEquals(directoryUser2.id, userId2)
    assertEquals(directoryUser1.groups!![0].id, groupId)
    assertEquals(directoryUser2.groups!![0].id, groupId)
  }

  @Test
  fun listDirectoryUsersWithGroupParamShouldReturnDirectoryUsers() {
    val workos = createWorkOSClient()

    val userId1 = "directory_user_01E1JJHG3BFJ3FNRRHSFWEBNCS"
    val userId2 = "directory_user_01E1JJHG10ANRA2V6PAX3GD7TE"
    val groupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_users",
      params = mapOf(
        "group" to equalTo(groupId),
      ),
      responseBody = """{
          "data": [{
            "id": "$userId1",
            "directory_id": "directoryId",
            "idp_id": "1902",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "jan@foo-corp.com"
            }],
            "first_name": "Jan",
            "last_name": "Brown",
            "username": "jan@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Engineering",
              "created_at": "2021-06-25T19:07:33.155Z",
              "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
           },
           {
            "id": "$userId2",
            "idp_id": "8953",
            "directory_id": "directoryId",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "rosalinda@foo-corp.com"
            }],
            "first_name": "Rosalinda",
            "last_name": "Swift",
            "username": "rosalinda@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Engineering",
              "created_at": "2021-06-25T19:07:33.155Z",
              "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
          }],
          "object": "list",
          "list_metadata": {
            "after": "directory_user_01E4RH82CC8QAP8JTRCTNDSS4C",
            "before": "directory_user_01E4RH828021B9ZZB8KH8E2Z1W"
          }
        }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryUserOptions
      .builder()
      .group(groupId)
      .build()

    val (data) = workos.directorySync.listDirectoryUsers(listOptions)

    val directoryUser1 = data[0]
    val directoryUser2 = data[1]

    assertEquals(directoryUser1.id, userId1)
    assertEquals(directoryUser2.id, userId2)
    assertEquals(directoryUser1.groups!![0].id, groupId)
    assertEquals(directoryUser2.groups!![0].id, groupId)
  }

  @Test
  fun listDirectoryUsersWithRawParamsShouldReturnDirectoryUsers() {
    val workos = createWorkOSClient()

    val userId1 = "directory_user_01E1JJHG3BFJ3FNRRHSFWEBNCS"
    val userId2 = "directory_user_01E1JJHG10ANRA2V6PAX3GD7TE"
    val groupId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_users",
      params = mapOf(
        "group" to equalTo(groupId),
        "after" to equalTo("after"),
        "before" to equalTo("before"),
        "limit" to equalTo("10")
      ),
      responseBody = """{
          "data": [{
            "id": "$userId1",
            "idp_id": "1902",
            "directory_id": "directoryId",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "jan@foo-corp.com"
            }],
            "first_name": "Jan",
            "last_name": "Brown",
            "username": "jan@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Engineering",
              "created_at": "2021-06-25T19:07:33.155Z",
              "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
           },
           {
            "id": "$userId2",
            "idp_id": "8953",
            "directory_id": "directoryId",
            "emails": [{
              "primary": true,
              "type": "work",
              "value": "rosalinda@foo-corp.com"
            }],
            "first_name": "Rosalinda",
            "last_name": "Swift",
            "username": "rosalinda@foo-corp.com",
            "groups": [{
              "id": "$groupId",
              "idp_id": "02grqrue4294w24",
              "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Engineering",
              "created_at": "2021-06-25T19:07:33.155Z",
              "updated_at": "2021-06-25T19:07:33.155Z",
              "raw_attributes": {}
            }],
            "state": "active",
            "custom_attributes": {
              "department": "Engineering"
            },
            "raw_attributes": {}
          }],
          "object": "list",
          "list_metadata": {
            "after": "directory_user_01E4RH82CC8QAP8JTRCTNDSS4C",
            "before": "directory_user_01E4RH828021B9ZZB8KH8E2Z1W"
          }
        }""",
      responseStatus = 200,
    )

    val listOptions = DirectorySyncApi.ListDirectoryUserOptions(
      group = groupId,
      after = "after",
      before = "before",
      limit = 10
    )

    val (data) = workos.directorySync.listDirectoryUsers(listOptions)

    val directoryUser1 = data[0]
    val directoryUser2 = data[1]

    assertEquals(directoryUser1.id, userId1)
    assertEquals(directoryUser2.id, userId2)
    assertEquals(directoryUser1.groups!![0].id, groupId)
    assertEquals(directoryUser2.groups!![0].id, groupId)
  }

  @Test
  fun listDirectoryUserThrowsWithNoParams() {
    assertThrows(IllegalArgumentException::class.java) {
      val workos = createWorkOSClient()
      workos.directorySync.listDirectoryUsers(DirectorySyncApi.ListDirectoryUserOptions())
    }
  }

  @Test
  fun listDirectoryGroupThrowsWithNoParams() {
    assertThrows(IllegalArgumentException::class.java) {
      val workos = createWorkOSClient()
      workos.directorySync.listDirectoryGroups(DirectorySyncApi.ListDirectoryGroupOptions())
    }
  }
}
