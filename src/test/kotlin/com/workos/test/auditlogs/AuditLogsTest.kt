package com.workos.test.auditlogs

import com.workos.auditlogs.AuditLogsApi
import com.workos.auditlogs.models.AuditLogExportState
import com.workos.common.exceptions.BadRequestException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class AuditLogsTest : TestBase() {
  @Test
  fun createEventShouldNotThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/events",
      responseBody = """{
        "success": true
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "event": {
          "occurred_at": "1970-01-15T02:57:07.200Z",
          "action": "user.signed_in",
          "actor": {
            "id": "user_123",
            "type": "user"
          },
          "targets": [
            {
              "id": "team_123",
              "type": "team"
            }
          ],
          "context": {
            "location": "0.0.0.0"
          },
          "version": 1
        }
      }"""
    )

    val options = AuditLogsApi.CreateAuditLogEventOptions.builder()
      .action("user.signed_in")
      .occurredAt(Date(1220227200))
      .actor("user_123", "user")
      .target("team_123", "team")
      .context("0.0.0.0")
      .build()

    workos.auditLogs.createEvent("org_123", options)
  }

  fun createEventWithAllOptionsShouldNotThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/events",
      responseBody = """{
        "success": true
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "event": {
          "occurred_at": "1970-01-15T02:57:07.200Z",
          "action": "user.signed_in",
          "actor": {
            "id": "user_123",
            "type": "user",
            "name": "User",
            "metadata": {
              "role": "admin"
            }
          },
          "targets": [
            {
              "id": "team_123",
              "type": "team",
              "name": "Team",
              "metadata": {
                "foo": "foo",
                "bar": "bar"
              }
            },
            {
              "id": "team_abc",
              "type": "team",
              "name": "Another Team"
            }
          ],
          "context": {
            "location": "0.0.0.0",
            "user_agent": "User Agent"
          },
          "version": 1,
          "metadata": {
            "client": "web"
          }
        }
      }"""
    )

    val options = AuditLogsApi.CreateAuditLogEventOptions.builder()
      .action("user.signed_in")
      .occurredAt(Date(1220227200))
      .actor(
        "user_123",
        "user",
        "User",
        mapOf(
          "role" to "admin"
        )
      )
      .target(
        "team_123",
        "team",
        "Team",
        mapOf(
          "foo" to "foo",
          "bar" to "bar"
        )
      )
      .target("team_abc", "team", "Another Team")
      .context("0.0.0.0", "User Agent")
      .metadata(
        mapOf(
          "client" to "web"
        )
      )
      .build()

    workos.auditLogs.createEvent("org_123", options)
  }

  @Test
  fun createEventShouldPassIdempotencyKey() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/events",
      responseBody = """{
        "success": true
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "event": {
          "occurred_at": "1970-01-15T02:57:07.200Z",
          "action": "user.signed_in",
          "actor": {
            "id": "user_123",
            "type": "user"
          },
          "targets": [
            {
              "id": "team_123",
              "type": "team"
            }
          ],
          "context": {
            "location": "0.0.0.0"
          },
          "version": 1
        }
      }""",
      requestHeaders = mapOf("Idempotency-Key" to "some-idempotency-key-value")
    )

    val event = AuditLogsApi.CreateAuditLogEventOptions.builder()
      .action("user.signed_in")
      .occurredAt(Date(1220227200))
      .actor("user_123", "user")
      .target("team_123", "team")
      .context("0.0.0.0")
      .build()

    val requestOptions = AuditLogsApi.CreateAuditLogEventRequestOptions.builder()
      .idempotencyKey("some-idempotency-key-value")
      .build()

    workos.auditLogs.createEvent("org_123", event, requestOptions)
  }

  @Test
  fun createEventShouldThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/events",
      responseBody = """{
        "code": "invalid_audit_log",
        "message": "Invalid Audit Log event",
        "errors": [{
          "instancePath": "/targets/0/type",
          "schemaPath": "#/properties/targets/allOf/0/contains/properties/type/const",
          "keyword": "const",
          "params": {
            "allowValues": ["team"]
          },
          "message": "must be equal to constant",
          "schema": "team",
          "parentSchema": {
            "enum": ["team"],
            "type": "string"
          },
          "data": "user"
        }]
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "event": {
          "occurred_at": "1970-01-15T02:57:07.200Z",
          "action": "user.signed_in",
          "actor": {
            "id": "user_123",
            "type": "user"
          },
          "targets": [
            {
              "id": "team_123",
              "type": "team"
            }
          ],
          "context": {
            "location": "0.0.0.0"
          },
          "version": 1
        }
      }""",
      responseStatus = 400
    )

    val options = AuditLogsApi.CreateAuditLogEventOptions.builder()
      .action("user.signed_in")
      .occurredAt(Date(1220227200))
      .actor("user_123", "user")
      .target("team_123", "team")
      .context("0.0.0.0")
      .build()

    assertThrows(BadRequestException::class.java) {
      workos.auditLogs.createEvent("org_123", options)
    }

    try {
      workos.auditLogs.createEvent("org_123", options)
    } catch (exception: BadRequestException) {
      assertEquals(400, exception.status)
      assertEquals("invalid_audit_log", exception.code)
      assertEquals("Invalid Audit Log event", exception.message)
      assert(exception.errors?.size == 1)
    }
  }

  @Test
  fun createExportShouldNotThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/exports",
      responseBody = """{
        "object": "audit_log_export",
        "id": "audit_log_export_123",
        "state": "pending",
        "created_at": "2022-08-17T19:58:50.686Z",
        "updated_at": "2022-08-17T19:58:50.686Z"
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "range_start": "2022-07-17T19:58:50.686Z",
        "range_end": "2022-09-17T19:58:50.686Z"
      }"""
    )

    val options = AuditLogsApi.CreateAuditLogExportOptions.builder()
      .organizationId("org_123")
      .rangeStart(Date(1658087930686))
      .rangeEnd(Date(1663444730686))
      .build()

    val export = workos.auditLogs.createExport(options)

    assertEquals("audit_log_export", export.obj)
    assertEquals("audit_log_export_123", export.id)
    assertEquals(AuditLogExportState.Pending, export.state)
    assertEquals(Date(1660766330686), export.createdAt)
    assertEquals(Date(1660766330686), export.updatedAt)
  }

  @Test
  fun createExportWithAllOptionShouldNotThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/exports",
      responseBody = """{
        "object": "audit_log_export",
        "id": "audit_log_export_123",
        "state": "ready",
        "url": "https://audit-logs.com/download.csv",
        "created_at": "2022-08-17T19:58:50.686Z",
        "updated_at": "2022-08-17T19:58:50.686Z"
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "range_start": "2022-07-17T19:58:50.686Z",
        "range_end": "2022-09-17T19:58:50.686Z",
        "actions": ["foo", "bar"],
        "actors": ["foo", "bar"],
        "targets": ["foo", "bar"]
      }"""
    )

    val options = AuditLogsApi.CreateAuditLogExportOptions.builder()
      .organizationId("org_123")
      .rangeStart(Date(1658087930686))
      .rangeEnd(Date(1663444730686))
      .actions(listOf("foo", "bar"))
      .actors(listOf("foo", "bar"))
      .targets(listOf("foo", "bar"))
      .build()

    val export = workos.auditLogs.createExport(options)

    assertEquals("audit_log_export", export.obj)
    assertEquals("audit_log_export_123", export.id)
    assertEquals(AuditLogExportState.Ready, export.state)
    assertEquals("https://audit-logs.com/download.csv", export.url)
    assertEquals(Date(1660766330686), export.createdAt)
    assertEquals(Date(1660766330686), export.updatedAt)
  }

  @Test
  fun createExportShouldThrowInvalidDateRangeException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/exports",
      responseBody = """{
        "code": "invalid_date_range_exception",
        "message": "Start date cannot be before 2022-05-17T00:00:00.000Z"
      }""",
      requestBody = """{
        "organization_id": "org_123",
        "range_start": "2022-07-17T19:58:50.686Z",
        "range_end": "2022-09-17T19:58:50.686Z"
      }""",
      responseStatus = 400
    )

    val options = AuditLogsApi.CreateAuditLogExportOptions.builder()
      .organizationId("org_123")
      .rangeStart(Date(1658087930686))
      .rangeEnd(Date(1663444730686))
      .build()

    assertThrows(BadRequestException::class.java) {
      workos.auditLogs.createExport(options)
    }

    try {
      workos.auditLogs.createExport(options)
    } catch (exception: BadRequestException) {
      assertEquals(400, exception.status)
      assertEquals("invalid_date_range_exception", exception.code)
      assertEquals("Start date cannot be before 2022-05-17T00:00:00.000Z", exception.message)
    }
  }

  @Test
  fun getExportShouldNotThrowException() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs/exports/audit_log_export_123",
      responseBody = """{
        "object": "audit_log_export",
        "id": "audit_log_export_123",
        "state": "pending",
        "created_at": "2022-08-17T19:58:50.686Z",
        "updated_at": "2022-08-17T19:58:50.686Z"
      }"""
    )

    val export = workos.auditLogs.getExport("audit_log_export_123")

    assertEquals("audit_log_export", export.obj)
    assertEquals("audit_log_export_123", export.id)
    assertEquals(AuditLogExportState.Pending, export.state)
    assertEquals(Date(1660766330686), export.createdAt)
    assertEquals(Date(1660766330686), export.updatedAt)
  }
}
