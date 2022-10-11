package com.workos.test.directorysync
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest : TestBase() {
  @Test
  fun userPrimaryMethodReturn() {
    val workos = createWorkOSClient()
    val userId = "directory_user_01E1JG7J09H96KYP8HM9B0G5SJ"
    val gsuiteDirectoryId = "directory_01ECAZ4NV9QMV47GW873HDCX74"

    stubResponse(
      url = "/directory_users/$userId",
      responseBody = """{
        "id": "$userId",
        "idp_id": "2836",
        "directory_id": "$gsuiteDirectoryId",
        "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
        "emails": [{
          "primary": true,
          "type": "work",
          "value": "marcelina1@foo-corp.com"
        },
        {
          "primary": false,
          "type": "work",
          "value": "marcelina2@foo-corp.com"
        }],
        "first_name": "Marcelina",
        "last_name": "Davis",
        "job_title": "Software Engineer",
        "username": "marcelina@foo-corp.com",
        "groups": [{
          "id": "directory_group_01E64QTDNS0EGJ0FMCVY9BWGZT",
          "idp_id": "02grqrue4294w24",
          "directory_id": "$gsuiteDirectoryId",
          "name": "Engineering",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z",
          "raw_attributes": {}
        }],
        "state": "active",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:08:33.155Z",
        "custom_attributes": {
          "department": "Engineering"
        },
        "raw_attributes": {}
      }"""
    )
    val directoryUser = workos.directorySync.getDirectoryUser(userId)
    assertEquals(directoryUser.primaryEmail(), "marcelina1@foo-corp.com")
  }
}
