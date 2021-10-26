package com.workos.directorysync

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
            "/directories",
            """{
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
                }]
            }"""
        )

        val (data) = workos.directorySync.listDirectories()
        val gsuiteDirectory = data[0]
        val oktaDirectory = data[1]

        assertEquals(gsuiteDirectory.id, gsuiteDirectoryId)
        assertEquals(oktaDirectory.id, oktaDirectoryId)
        assertEquals(gsuiteDirectory.type, DirectoryType.GSuiteDirectory)
        assertEquals(oktaDirectory.type, DirectoryType.OktaSCIMV2_0)
    }
}
