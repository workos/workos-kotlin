package com.workos.test.events

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.models.Order
import com.workos.events.EventsApi
import com.workos.events.models.DirectoryEvent
import com.workos.events.models.DirectoryGroupEvent
import com.workos.events.models.DirectoryGroupMembershipEvent
import com.workos.events.models.DirectoryUserEvent
import com.workos.events.models.OrganizationEvent
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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
    assertTrue(eventList.data.isEmpty())
  }

  @Test
  fun listEventsShouldReturnOrganizationEvent() {
    val workos = createWorkOSClient()

    stubResponse(
      "/events",
      """{
        "object": "list",
        "data": [
          {
            "object": "event",
            "id": "event_01H2GNQD5D7ZE06FDDS75NFPHZ",
            "event": "organization.created",
            "data": {
              "id": "org_01EZTR6WYX1A0DSE2CYMGXQ24Y",
              "name": "Test Org"
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
    assertEquals(1, eventList.data.size)
    val event = eventList.data[0]
    assertIs<OrganizationEvent>(event)
    assertEquals("organization.created", event.event)
    assertEquals("org_01EZTR6WYX1A0DSE2CYMGXQ24Y", (event as OrganizationEvent).data.id)
    assertEquals("Test Org", event.data.name)
  }

  @Test
  fun listEventsShouldReturnDirectoryEvent() {
    val workos = createWorkOSClient()

    stubResponse(
      "/events",
      """{
        "object": "list",
        "data": [
          {
            "object": "event",
            "id": "event_01H2GNQD5D7ZE06FDDS75NFPHZ",
            "event": "dsync.created",
            "data": {
              "id": "directory_01ECAZ4NV9QMV47GW873HDCX74",
              "name": "Test Directory"
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
    assertEquals(1, eventList.data.size)
    val event = eventList.data[0]
    assertIs<DirectoryEvent>(event)
    assertEquals("dsync.created", event.event)
    assertEquals("directory_01ECAZ4NV9QMV47GW873HDCX74", (event as DirectoryEvent).data.id)
    assertEquals("Test Directory", event.data.name)
  }

  @Test
  fun listEventsShouldReturnDirectoryGroupEvent() {
    val workos = createWorkOSClient()

    stubResponse(
      "/events",
      """{
        "object": "list",
        "data": [
          {
            "object": "event",
            "id": "event_01H2GNQD5D7ZE06FDDS75NFPHZ",
            "event": "dsync.group.created",
            "data": {
              "id": "directory_group_01E1X5GPMMXF4T1DCERMVEEPVW",
              "name": "Developers"
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
    assertEquals(1, eventList.data.size)
    val event = eventList.data[0]
    assertIs<DirectoryGroupEvent>(event)
    assertEquals("dsync.group.created", event.event)
    assertEquals("directory_group_01E1X5GPMMXF4T1DCERMVEEPVW", (event as DirectoryGroupEvent).data.id)
    assertEquals("Developers", event.data.name)
  }

  @Test
  fun listEventsShouldReturnDirectoryUserEvent() {
    val workos = createWorkOSClient()

    stubResponse(
      "/events",
      """{
        "object": "list",
        "data": [
          {
            "object": "event",
            "id": "event_01H2GNQD5D7ZE06FDDS75NFPHZ",
            "event": "dsync.user.created",
            "data": {
              "id": "directory_user_01E1X56GH84T3FB41SD6PZGDBX",
              "email": "eric@example.com"
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
    assertEquals(1, eventList.data.size)
    val event = eventList.data[0]
    assertIs<DirectoryUserEvent>(event)
    assertEquals("dsync.user.created", event.event)
    assertEquals("directory_user_01E1X56GH84T3FB41SD6PZGDBX", (event as DirectoryUserEvent).data.id)
  }
}
