package com.workos.test.auditlogs

import com.workos.auditlogs.AuditLogsApi
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
      url = "/audit_logs",
      responseBody = """{
        "success": true
      }""",
      requestBody = """{
<<<<<<< HEAD
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
=======
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
>>>>>>> ffa1495 (Format occurred_at as ISO string)
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
      url = "/audit_logs",
      responseBody = """{
        "success": true
      }""",
      requestBody = """{
<<<<<<< HEAD
        "organization_id": "org_123",
        "event": {
          "occurred_at": "1970-01-15T02:57:07.200Z",
          "action": "user.signed_in",
          "actor": {
            "id": "user_123",
            "type": "user",
            "name": "User",
=======
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
>>>>>>> ffa1495 (Format occurred_at as ISO string)
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
        "user_123", "user", "User",
        mapOf(
          "role" to "admin",
        )
      )
      .target(
        "team_123", "team", "Team",
        mapOf(
          "foo" to "foo",
          "bar" to "bar"
        )
      )
      .target("team_abc", "team", "Another Team")
      .context("0.0.0.0", "User Agent")
      .metadata(
        mapOf(
          "client" to "web",
        )
      )
      .build()

    workos.auditLogs.createEvent("org_123", options)
  }

  @Test
  fun createEventShouldPassIdempotencyKey() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/audit_logs",
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
      url = "/audit_logs",
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
<<<<<<< HEAD
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
=======
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
>>>>>>> ffa1495 (Format occurred_at as ISO string)
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
}
