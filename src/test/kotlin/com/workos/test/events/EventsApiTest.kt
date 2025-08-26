package com.workos.test.events

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.models.Order
import com.workos.events.EventsApi
import com.workos.events.models.DirectoryGroupMembershipEvent
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventsApiTest : TestBase() {
  @Test
  fun listEventsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      "/events",
      """{
        "object": "list",
        "data": [
          {
            "object": "event",
            "id": "event_01H2GNQD5D7ZE06FDDS75NFPHY",
            "event": "dsync.group.user_added",
            "data": {
              "user": {
                "id": "directory_user_01E1X56GH84T3FB41SD6PZGDBX",
                "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
                "organization_id": "org_01EZTR6WYX1A0DSE2CYMGXQ24Y",
                "idp_id": "2936",
                "first_name": "Eric",
                "last_name": "Schneider",
                "email": "eric@example.com",
                "state": "active",
                "created_at": "2021-06-25T19:07:33.155Z",
                "updated_at": "2021-06-25T19:07:33.155Z",
                "custom_attributes": {
                  "department": "Engineering",
                  "job_title": "Software Engineer"
                },
                "role": { "slug": "member" },
                "raw_attributes": {}
              },
              "group": {
                "id": "directory_group_01E1X5GPMMXF4T1DCERMVEEPVW",
                "idp_id": "02grqrue4294w24",
                "directory_id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
                "organization_id": "org_01EZTR6WYX1A0DSE2CYMGXQ24Y",
                "name": "Developers",
                "created_at": "2021-06-25T19:07:33.155Z",
                "updated_at": "2021-06-25T19:07:33.155Z",
                "raw_attributes": {}
              }
            },
            "created_at": "2023-06-09T18:12:01.837Z"
          }
        ],
        "list_metadata": {
          "after": "event_01H2GQNMQNH8VRXVR7AEYG9XCJ"
        }
      }"""
    )

    val eventList = workos.events.listEvents()

    assertEquals("list", eventList.obj)
    assertEquals("event_01H2GQNMQNH8VRXVR7AEYG9XCJ", eventList.listMetadata.after)

    val events = eventList.data
    assertEquals(1, events.size)
    val event = events[0]
    assertEquals("event", event.obj)
    assertEquals("event_01H2GNQD5D7ZE06FDDS75NFPHY", event.id)
    assertEquals("dsync.group.user_added", event.event)
    assertEquals("2023-06-09T18:12:01.837Z", event.createdAt)

    assertIs<DirectoryGroupMembershipEvent>(event)
    val membershipEvent = event as DirectoryGroupMembershipEvent
    assertEquals("directory_user_01E1X56GH84T3FB41SD6PZGDBX", membershipEvent.data.user.id)
    assertEquals("directory_group_01E1X5GPMMXF4T1DCERMVEEPVW", membershipEvent.data.group.id)
  }

  @Test
  fun listEventsWithQueryParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/events",
      params = mapOf(
        "events" to equalTo("dsync.group.user_added,dsync.group.user_removed"),
        "organization_id" to equalTo("org_123"),
        "after" to equalTo("after_cursor"),
        "before" to equalTo("before_cursor"),
        "limit" to equalTo("5"),
        "order" to equalTo("asc")
      ),
      responseBody = """{
        "object": "list",
        "data": [],
        "list_metadata": {"after": null}
      }"""
    )

    val options = EventsApi.ListEventsOptions.builder()
      .events(listOf("dsync.group.user_added", "dsync.group.user_removed"))
      .organizationId("org_123")
      .after("after_cursor")
      .before("before_cursor")
      .limit(5)
      .order(Order.Asc)
      .build()

    val eventList = workos.events.listEvents(options)
    assertEquals("list", eventList.obj)
  }
}
