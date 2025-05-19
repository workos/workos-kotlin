package com.workos.test.fga

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.workos.common.exceptions.GenericServerException
import com.workos.common.models.ListMetadata
import com.workos.common.models.Order
import com.workos.fga.builders.CheckBatchOptionsBuilder
import com.workos.fga.builders.CheckOptionsBuilder
import com.workos.fga.builders.CreateResourceOptionsBuilder
import com.workos.fga.builders.CreateWarrantOptionsBuilder
import com.workos.fga.builders.DeleteWarrantOptionsBuilder
import com.workos.fga.builders.ListResourcesOptionsBuilder
import com.workos.fga.builders.ListWarrantsOptionsBuilder
import com.workos.fga.builders.QueryOptionsBuilder
import com.workos.fga.builders.UpdateResourceOptionsBuilder
import com.workos.fga.builders.WriteWarrantOptionsBuilder
import com.workos.fga.models.Resource
import com.workos.fga.models.Subject
import com.workos.fga.models.Warrant
import com.workos.fga.types.CheckRequestOptions
import com.workos.fga.types.QueryRequestOptions
import com.workos.fga.types.WarrantCheckOptions
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FgaApiTest : TestBase() {
  val workos = createWorkOSClient()

  @Test
  fun getResourceShouldReturnValidResourceObject() {
    stubResponse(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }"""
    )

    val resource = workos.fga.getResource("user", "user_123")

    assertEquals(
      Resource("user", "user_123"),
      resource
    )
  }

  @Test
  fun getResourceShouldFailAfterMaxRetries() {
    stubResponse(
      "/fga/v1/resources/user/user_123",
      """{
        "code": "internal_error",
        "message": "list error"
      }""",
      500
    )

    assertFailsWith<GenericServerException> {
      workos.fga.getResource("user", "user_123")
    }
  }

  @Test
  fun getResourceShouldReturnValidResourceAfterOneRetry() {
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      """{
        "code": "internal_error",
        "message": "list error"
      }""",
      500,
      emptyMap(),
      null,
      null,
      "Get resource",
      STARTED,
      "Error state"
    )
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }""",
      200,
      emptyMap(),
      null,
      null,
      "Get resource",
      "Error state",
      "Success state"
    )

    val resource = workos.fga.getResource("user", "user_123")

    assertEquals(
      Resource("user", "user_123"),
      resource
    )
  }

  @Test
  fun getResourceShouldReturnValidResourceObjectWithMeta() {
    stubResponse(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123",
        "meta": {
          "description": "Some user"
        }
      }"""
    )

    val resource = workos.fga.getResource("user", "user_123")

    assertEquals(
      Resource("user", "user_123", mapOf("description" to "Some user")),
      resource
    )
  }

  @Test
  fun listResourcesShouldReturnValidResources() {
    stubResponse(
      "/fga/v1/resources",
      """{
        "data": [
          {
            "resource_type": "user",
            "resource_id": "user_123"
          },
          {
            "resource_type": "user",
            "resource_id": "user_456",
            "meta": {
              "description": "Some user"
            }
          }
        ],
        "list_metadata": {
          "after": "user_456",
          "before": null
        }
      }"""
    )

    val resources = workos.fga.listResources(ListResourcesOptionsBuilder().build())

    assertEquals(
      Resource("user", "user_123"),
      resources.data[0]
    )
    assertEquals(
      Resource("user", "user_456", mapOf("description" to "Some user")),
      resources.data[1]
    )
    assertEquals(
      ListMetadata("user_456", null),
      resources.listMetadata
    )
  }

  @Test
  fun listResourcesShouldReturnValidResourcesWithPaginationAndFilters() {
    stubResponse(
      "/fga/v1/resources",
      """{
        "data": [
          {
            "resource_type": "user",
            "resource_id": "user_456"
          }
        ],
        "list_metadata": {
          "after": "user_456",
          "before": null
        }
      }"""
    )

    val resources = workos.fga.listResources(
      ListResourcesOptionsBuilder()
        .order(Order.Asc)
        .search("user_456")
        .limit(5)
        .build()
    )

    assertEquals(
      Resource("user", "user_456"),
      resources.data[0]
    )
    assertEquals(
      ListMetadata("user_456", null),
      resources.listMetadata
    )
  }

  @Test
  fun createResourceShouldReturnValidResourceObject() {
    stubResponse(
      "/fga/v1/resources",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }"""
    )

    val options =
      CreateResourceOptionsBuilder("user", "user_123")
        .build()
    val resource = workos.fga.createResource(options)

    assertEquals(
      Resource("user", "user_123"),
      resource
    )
  }

  @Test
  fun createResourceShouldFailAfterMaxRetries() {
    stubResponse(
      "/fga/v1/resources",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }""",
      504
    )

    val options =
      CreateResourceOptionsBuilder("user", "user_123")
        .build()

    assertFailsWith<GenericServerException> {
      workos.fga.createResource(options)
    }
  }

  @Test
  fun createResourceShouldReturnValidResourceObjectAfterOneRetry() {
    stubResponseWithScenario(
      "/fga/v1/resources",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }""",
      502,
      scenarioName = "Create resource",
      scenarioState = STARTED,
      nextScenarioState = "Error state"
    )
    stubResponseWithScenario(
      "/fga/v1/resources",
      """{
        "resource_type": "user",
        "resource_id": "user_123"
      }""",
      scenarioName = "Create resource",
      scenarioState = "Error state",
      nextScenarioState = "Success state"
    )

    val options =
      CreateResourceOptionsBuilder("user", "user_123")
        .build()
    val resource = workos.fga.createResource(options)

    assertEquals(
      Resource("user", "user_123"),
      resource
    )
  }

  @Test
  fun updateResourceShouldReturnValidResourceObject() {
    stubResponse(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123",
        "meta": {
          "description": "Some user"
        }
      }""",
      requestBody =
      """{
        "meta": {
          "description": "Some user"
        }
      }"""
    )

    val options =
      UpdateResourceOptionsBuilder()
        .meta(mapOf("description" to "Some user"))
        .build()

    val resource = workos.fga.updateResource("user", "user_123", options)

    assertEquals(
      Resource("user", "user_123", mapOf("description" to "Some user")),
      resource
    )
  }

  @Test
  fun updateResourceShouldFailAfterMaxRetries() {
    stubResponse(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123",
        "meta": {
          "description": "Some user"
        }
      }""",
      500,
      requestBody =
      """{
        "meta": {
          "description": "Some user"
        }
      }"""
    )

    val options =
      UpdateResourceOptionsBuilder()
        .meta(mapOf("description" to "Some user"))
        .build()

    assertFailsWith<GenericServerException> {
      workos.fga.updateResource("user", "user_123", options)
    }
  }

  @Test
  fun updateResourceShouldReturnValidResourceObjectAfterOneRetry() {
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123",
        "meta": {
          "description": "Some user"
        }
      }""",
      responseStatus = 500,
      requestBody =
      """{
        "meta": {
          "description": "Some user"
        }
      }""",
      scenarioName = "Update resource",
      scenarioState = STARTED,
      nextScenarioState = "Error state"
    )
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      """{
        "resource_type": "user",
        "resource_id": "user_123",
        "meta": {
          "description": "Some user"
        }
      }""",
      requestBody =
      """{
        "meta": {
          "description": "Some user"
        }
      }""",
      scenarioName = "Update resource",
      scenarioState = "Error state",
      nextScenarioState = "Success state"
    )

    val options =
      UpdateResourceOptionsBuilder()
        .meta(mapOf("description" to "Some user"))
        .build()

    val resource = workos.fga.updateResource("user", "user_123", options)

    assertEquals(
      Resource("user", "user_123", mapOf("description" to "Some user")),
      resource
    )
  }

  @Test
  fun deleteResourceShouldWorkAndReturnNothing() {
    stubResponse("/fga/v1/resources/user/user_123", "")

    assertDoesNotThrow() { workos.fga.deleteResource("user", "user_123") }
  }

  @Test
  fun deleteResourceShouldFailAfterMaxRetries() {
    stubResponse("/fga/v1/resources/user/user_123", "", 500)

    assertFailsWith<GenericServerException> {
      workos.fga.deleteResource("user", "user_123")
    }
  }

  @Test
  fun deleteResourceShouldWorkAndReturnNothingAfterOneRetry() {
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      "",
      500,
      scenarioName = "Delete resource",
      scenarioState = STARTED,
      nextScenarioState = "Error state"
    )
    stubResponseWithScenario(
      "/fga/v1/resources/user/user_123",
      "",
      scenarioName = "Delete resource",
      scenarioState = "Error state",
      nextScenarioState = "Success state"
    )

    assertDoesNotThrow() {
      workos.fga.deleteResource("user", "user_123")
    }
  }

  @Test
  fun listWarrantsShouldReturnValidWarrants() {
    stubResponse(
      "/fga/v1/warrants",
      """{
        "data": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          }
        ],
        "list_metadata": {
          "after": "eyJpZCI6IntcInBrXCI6XCJ0mlyb25tZW50XzAxSjBLWF",
          "before": null
        }
      }"""
    )

    val warrants = workos.fga.listWarrants(ListWarrantsOptionsBuilder().build())

    assertEquals(
      Warrant(
        "role",
        "admin",
        "member",
        Subject("user", "tony-stark")
      ),
      warrants.data[0]
    )
    assertEquals(ListMetadata("eyJpZCI6IntcInBrXCI6XCJ0mlyb25tZW50XzAxSjBLWF", null), warrants.listMetadata)
  }

  @Test
  fun listWarrantsShouldReturnValidWarrantsWithPaginationAndFilters() {
    stubResponse(
      "/fga/v1/warrants",
      """{
        "data": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          }
        ],
        "list_metadata": {
          "after": "eyJpZCI6IntcInBrXCI6XCJ0mlyb25tZW50XzAxSjBLWF",
          "before": null
        }
      }"""
    )

    val options = ListWarrantsOptionsBuilder()
      .resourceType("role")
      .resourceId("admin")
      .relation("member")
      .subjectType("user")
      .subjectId("tony-stark")
      .subjectRelation("member")
      .limit(5)
      .after("someAfterId")
      .build()
    val warrants = workos.fga.listWarrants(options)

    assertEquals(
      Warrant(
        "role",
        "admin",
        "member",
        Subject("user", "tony-stark")
      ),
      warrants.data[0]
    )
    assertEquals(ListMetadata("eyJpZCI6IntcInBrXCI6XCJ0mlyb25tZW50XzAxSjBLWF", null), warrants.listMetadata)
  }

  @Test
  fun writeWarrantWithCreateOpShouldReturnWarrantResponse() {
    stubResponse(
      "/fga/v1/warrants",
      """{
        "warrant_token": "new_token"
      }""",
      requestBody =
      """{
        "op": "create",
        "resource_type": "role",
        "resource_id": "admin",
        "relation": "member",
        "subject": {
          "resource_type": "user",
          "resource_id": "tony-stark"
        }
      }"""
    )

    val options =
      WriteWarrantOptionsBuilder("create", "role", "admin", "member", Subject("user", "tony-stark"))
        .build()

    val warrantResponse = workos.fga.writeWarrant(options)

    assertEquals("new_token", warrantResponse.warrantToken)
  }

  @Test
  fun writeWarrantWithDeleteOpShouldReturnWarrantResponse() {
    stubResponse(
      "/fga/v1/warrants",
      """{
        "warrant_token": "new_token"
      }""",
      requestBody =
      """{
        "op": "delete",
        "resource_type": "role",
        "resource_id": "admin",
        "relation": "member",
        "subject": {
          "resource_type": "user",
          "resource_id": "tony-stark"
        }
      }"""
    )

    val options =
      WriteWarrantOptionsBuilder("delete", "role", "admin", "member", Subject("user", "tony-stark"))
        .build()

    val warrantResponse = workos.fga.writeWarrant(options)

    assertEquals("new_token", warrantResponse.warrantToken)
  }

  @Test
  fun batchWriteWarrantsShouldReturnWarrantResponse() {
    stubResponse(
      "/fga/v1/warrants",
      """{
        "warrant_token": "new_token"
      }""",
      requestBody =
      """[
        {
          "op": "delete",
          "resource_type": "role",
          "resource_id": "editor",
          "relation": "member",
          "subject": {
            "resource_type": "user",
            "resource_id": "tony-stark"
          }
        },
        {
          "op": "delete",
          "resource_type": "tenant",
          "resource_id": "avengers",
          "relation": "member",
          "subject": {
            "resource_type": "user",
            "resource_id": "tony-stark"
          }
        },
        {
          "op": "create",
          "resource_type": "role",
          "resource_id": "admin",
          "relation": "member",
          "subject": {
            "resource_type": "user",
            "resource_id": "tony-stark"
          }
        },
        {
          "op": "create",
          "resource_type": "tenant",
          "resource_id": "stark-industries",
          "relation": "member",
          "subject": {
            "resource_type": "user",
            "resource_id": "tony-stark"
          }
        }
      ]"""
    )

    val options = listOf(
      WriteWarrantOptionsBuilder("delete", "role", "editor", "member", Subject("user", "tony-stark")).build(),
      DeleteWarrantOptionsBuilder("tenant", "avengers", "member", Subject("user", "tony-stark")).build(),
      WriteWarrantOptionsBuilder("create", "role", "admin", "member", Subject("user", "tony-stark")).build(),
      CreateWarrantOptionsBuilder("tenant", "stark-industries", "member", Subject("user", "tony-stark")).build()
    )

    val warrantResponse = workos.fga.batchWriteWarrants(options)

    assertEquals("new_token", warrantResponse.warrantToken)
  }

  @Test
  fun checkShouldReturnValidCheckResponse() {
    stubResponse(
      "/fga/v1/check",
      """{
        "result": "authorized",
        "is_implicit": false
      }""",
      requestBody =
      """{
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            },
            "context": {
              "role": "admin"
            }
          }
        ]
      }"""
    )

    val options = CheckOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark"),
          mapOf("role" to "admin")
        )
      )
    ).build()

    val checkResponse = workos.fga.check(options)

    assertEquals(true, checkResponse.authorized())
    assertEquals(false, checkResponse.isImplicit)
  }

  @Test
  fun checkWithRequestOptionsShouldReturnValidCheckResponse() {
    stubResponse(
      "/fga/v1/check",
      """{
        "result": "authorized",
        "is_implicit": false
      }""",
      requestBody =
      """{
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            },
            "context": {
              "role": "admin"
            }
          }
        ]
      }""",
      requestHeaders = mapOf("Warrant-Token" to "some-token")
    )

    val options = CheckOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark"),
          mapOf("role" to "admin")
        )
      )
    ).build()
    val requestOptions = CheckRequestOptions("some-token")

    val checkResponse = workos.fga.check(options, requestOptions)

    assertEquals(true, checkResponse.authorized())
    assertEquals(false, checkResponse.isImplicit)
  }

  @Test
  fun checkWithDebugShouldReturnValidCheckResponse() {
    stubResponse(
      "/fga/v1/check",
      """{
        "result": "authorized",
        "is_implicit": true,
        "debug_info": {
          "processing_time": 15665292,
          "decision_tree": {
            "check": {
                "resource_type": "role",
                "resource_id": "admin",
                "relation": "member",
                "subject": {
                    "resource_type": "user",
                    "resource_id": "tony-stark"
                },
                "context": null
            },
            "decision": "matched",
            "processing_time": 6235333,
            "children": [
                {
                    "check": {
                        "resource_type": "role",
                        "resource_id": "admin",
                        "relation": "member",
                        "subject": {
                            "resource_type": "tenant",
                            "resource_id": "stark-industries"
                        },
                        "context": null
                    },
                    "decision": "matched",
                    "processing_time": 2757333,
                    "children": [
                        {
                            "check": {
                                "resource_type": "tenant",
                                "resource_id": "stark-industries",
                                "relation": "member",
                                "subject": {
                                    "resource_type": "user",
                                    "resource_id": "tony-stark"
                                },
                                "context": null
                            },
                            "decision": "matched",
                            "processing_time": 3421625,
                            "children": null
                        }
                    ]
                }
            ]
          }
        }
      }""",
      requestBody =
      """{
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            },
            "context": {
              "role": "admin"
            }
          }
        ],
        "debug": true
      }"""
    )

    val options = CheckOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark"),
          mapOf("role" to "admin")
        )
      ),
      true
    ).build()

    val checkResponse = workos.fga.check(options)

    assertEquals(true, checkResponse.authorized())
    assertEquals(true, checkResponse.isImplicit)
    assertNotNull(checkResponse.debugInfo)
    assertNotNull(checkResponse.debugInfo?.processingTime)
    assertNotNull(checkResponse.debugInfo?.decisionTree)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.check)
    assertEquals("role", checkResponse.debugInfo?.decisionTree?.check?.resourceType)
    assertEquals("admin", checkResponse.debugInfo?.decisionTree?.check?.resourceId)
    assertEquals("member", checkResponse.debugInfo?.decisionTree?.check?.relation)
    assertEquals("user", checkResponse.debugInfo?.decisionTree?.check?.subject?.resourceType)
    assertEquals("tony-stark", checkResponse.debugInfo?.decisionTree?.check?.subject?.resourceId)
    assertEquals("matched", checkResponse.debugInfo?.decisionTree?.decision)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.processingTime)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children)
    assertEquals(1, checkResponse.debugInfo?.decisionTree?.children?.size)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0))
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check)
    assertEquals("role", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check?.resourceType)
    assertEquals("admin", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check?.resourceId)
    assertEquals("member", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check?.relation)
    assertEquals("tenant", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check?.subject?.resourceType)
    assertEquals("stark-industries", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.check?.subject?.resourceId)
    assertEquals("matched", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.decision)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.processingTime)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children)
    assertEquals(1, checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.size)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0))
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check)
    assertEquals("tenant", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.resourceType)
    assertEquals("stark-industries", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.resourceId)
    assertEquals("member", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.relation)
    assertEquals("user", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.subject?.resourceType)
    assertEquals("tony-stark", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.subject?.resourceId)
    assertEquals("matched", checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.decision)
    assertNotNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.processingTime)
    assertNull(checkResponse.debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.children)
  }

  @Test
  fun checkBatchShouldReturnValidCheckResponses() {
    stubResponse(
      "/fga/v1/check",
      """[
        {
          "result": "authorized",
          "is_implicit": false
        },
        {
          "result": "authorized",
          "is_implicit": true
        }
      ]""",
      requestBody =
      """{
        "op": "batch",
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          },
          {
            "resource_type": "tenant",
            "resource_id": "stark-industries",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          }
        ]
      }"""
    )

    val options = CheckBatchOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark")
        ),
        WarrantCheckOptions(
          "tenant",
          "stark-industries",
          "member",
          Subject("user", "tony-stark")
        )
      )
    ).build()

    val checkResponses = workos.fga.checkBatch(options)

    assertEquals(true, checkResponses[0].authorized())
    assertEquals(false, checkResponses[0].isImplicit)
    assertEquals(true, checkResponses[1].authorized())
    assertEquals(true, checkResponses[1].isImplicit)
  }

  @Test
  fun checkBatchWithRequestOptsShouldReturnValidCheckResponses() {
    stubResponse(
      "/fga/v1/check",
      """[
        {
          "result": "authorized",
          "is_implicit": false
        },
        {
          "result": "authorized",
          "is_implicit": true
        }
      ]""",
      requestBody =
      """{
        "op": "batch",
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          },
          {
            "resource_type": "tenant",
            "resource_id": "stark-industries",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          }
        ]
      }""",
      requestHeaders = mapOf("Warrant-Token" to "some-token")
    )

    val options = CheckBatchOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark")
        ),
        WarrantCheckOptions(
          "tenant",
          "stark-industries",
          "member",
          Subject("user", "tony-stark")
        )
      )
    ).build()
    val requestOptions = CheckRequestOptions("some-token")

    val checkResponses = workos.fga.checkBatch(options, requestOptions)

    assertEquals(true, checkResponses[0].authorized())
    assertEquals(false, checkResponses[0].isImplicit)
    assertEquals(true, checkResponses[1].authorized())
    assertEquals(true, checkResponses[1].isImplicit)
  }

  @Test
  fun checkBatchWithDebugShouldReturnValidCheckResponses() {
    stubResponse(
      "/fga/v1/check",
      """[
        {
          "result": "authorized",
          "is_implicit": true,
          "debug_info": {
            "processing_time": 17055584,
            "decision_tree": {
              "check": {
                "resource_type": "role",
                "resource_id": "admin",
                "relation": "member",
                "subject": {
                  "resource_type": "user",
                  "resource_id": "user1"
                },
                "context": null
              },
              "decision": "matched",
              "processing_time": 7921500,
              "children": [
                {
                  "check": {
                    "resource_type": "role",
                    "resource_id": "admin",
                    "relation": "member",
                    "subject": {
                      "resource_type": "tenant",
                      "resource_id": "stark-industries"
                    },
                    "context": null
                  },
                  "decision": "matched",
                  "processing_time": 3444209,
                  "children": [
                    {
                      "check": {
                        "resource_type": "tenant",
                        "resource_id": "stark-industries",
                        "relation": "member",
                        "subject": {
                          "resource_type": "user",
                          "resource_id": "tony-stark"
                        },
                        "context": null
                      },
                      "decision": "matched",
                      "processing_time": 4361875,
                      "children": null
                    }
                  ]
                }
              ]
            }
          }
        },
        {
          "result": "authorized",
          "is_implicit": false,
          "debug_info": {
            "processing_time": 8006583,
            "decision_tree": {
              "check": {
                "resource_type": "tenant",
                "resource_id": "stark-industries",
                "relation": "member",
                "subject": {
                  "resource_type": "user",
                  "resource_id": "tony-stark"
                },
                "context": null
              },
              "decision": "matched",
              "processing_time": 6379750,
              "children": null
            }
          }
        }
      ]""",
      requestBody =
      """{
        "op": "batch",
        "checks": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          },
          {
            "resource_type": "tenant",
            "resource_id": "stark-industries",
            "relation": "member",
            "subject": {
              "resource_type": "user",
              "resource_id": "tony-stark"
            }
          }
        ]
      }"""
    )

    val options = CheckBatchOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "role",
          "admin",
          "member",
          Subject("user", "tony-stark")
        ),
        WarrantCheckOptions(
          "tenant",
          "stark-industries",
          "member",
          Subject("user", "tony-stark")
        )
      )
    ).build()

    val checkResponses = workos.fga.checkBatch(options)

    assertEquals(true, checkResponses[0].authorized())
    assertEquals(true, checkResponses[0].isImplicit)
    assertNotNull(checkResponses[0].debugInfo)
    assertNotNull(checkResponses[0].debugInfo?.processingTime)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.check)
    assertEquals("role", checkResponses[0].debugInfo?.decisionTree?.check?.resourceType)
    assertEquals("admin", checkResponses[0].debugInfo?.decisionTree?.check?.resourceId)
    assertEquals("member", checkResponses[0].debugInfo?.decisionTree?.check?.relation)
    assertEquals("user", checkResponses[0].debugInfo?.decisionTree?.check?.subject?.resourceType)
    assertEquals("user1", checkResponses[0].debugInfo?.decisionTree?.check?.subject?.resourceId)
    assertNull(checkResponses[0].debugInfo?.decisionTree?.check?.context)
    assertEquals("matched", checkResponses[0].debugInfo?.decisionTree?.decision)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.processingTime)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children)
    assertEquals(1, checkResponses[0].debugInfo?.decisionTree?.children?.size)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0))
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check)
    assertEquals("role", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.resourceType)
    assertEquals("admin", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.resourceId)
    assertEquals("member", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.relation)
    assertEquals("tenant", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.subject?.resourceType)
    assertEquals("stark-industries", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.subject?.resourceId)
    assertNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.check?.context)
    assertEquals("matched", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.decision)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.processingTime)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children)
    assertEquals(1, checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.size)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0))
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check)
    assertEquals("tenant", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.resourceType)
    assertEquals("stark-industries", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.resourceId)
    assertEquals("member", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.relation)
    assertEquals("user", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.subject?.resourceType)
    assertEquals("tony-stark", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.subject?.resourceId)
    assertNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.check?.context)
    assertEquals("matched", checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.decision)
    assertNotNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.processingTime)
    assertNull(checkResponses[0].debugInfo?.decisionTree?.children?.get(0)?.children?.get(0)?.children)
    assertEquals(true, checkResponses[1].authorized())
    assertEquals(false, checkResponses[1].isImplicit)
    assertNotNull(checkResponses[1].debugInfo)
    assertEquals(8006583, checkResponses[1].debugInfo?.processingTime)
    assertNotNull(checkResponses[1].debugInfo?.decisionTree)
    assertNotNull(checkResponses[1].debugInfo?.decisionTree?.check)
    assertEquals("tenant", checkResponses[1].debugInfo?.decisionTree?.check?.resourceType)
    assertEquals("stark-industries", checkResponses[1].debugInfo?.decisionTree?.check?.resourceId)
    assertEquals("member", checkResponses[1].debugInfo?.decisionTree?.check?.relation)
    assertEquals("user", checkResponses[1].debugInfo?.decisionTree?.check?.subject?.resourceType)
    assertEquals("tony-stark", checkResponses[1].debugInfo?.decisionTree?.check?.subject?.resourceId)
    assertNull(checkResponses[1].debugInfo?.decisionTree?.check?.context)
    assertEquals("matched", checkResponses[1].debugInfo?.decisionTree?.decision)
    assertEquals(6379750, checkResponses[1].debugInfo?.decisionTree?.processingTime)
    assertNull(checkResponses[1].debugInfo?.decisionTree?.children)
  }

  @Test
  fun queryShouldReturnValidQueryResponse() {
    stubResponse(
      "/fga/v1/query",
      params = mapOf("q" to equalTo("select role where user:tony-stark is member")),
      responseBody = """{
        "data": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "warrant": {
              "resource_type": "role",
              "resource_id": "admin",
              "relation": "member",
              "subject": {
                "resource_type": "user",
                "resource_id": "tony-stark"
              }
            },
            "is_implicit": false,
            "meta": {
              "description": "Admin role"
            }
          }
        ],
        "list_metadata": {
          "before": null,
          "after": null
        }
      }""",
    )

    val options = QueryOptionsBuilder("select role where user:tony-stark is member").build()
    val queryResponse = workos.fga.query(options)

    assertEquals(1, queryResponse.data.size)
    assertEquals("role", queryResponse.data[0].resourceType)
    assertEquals("admin", queryResponse.data[0].resourceId)
    assertEquals("member", queryResponse.data[0].relation)
    assertEquals(Warrant("role", "admin", "member", Subject("user", "tony-stark")), queryResponse.data[0].warrant)
    assertEquals(false, queryResponse.data[0].isImplicit)
    assertNotNull(queryResponse.data[0].meta)
    assertEquals("Admin role", queryResponse.data[0].meta?.get("description"))
    assertNull(queryResponse.listMetadata.before)
    assertNull(queryResponse.listMetadata.after)
  }

  @Test
  fun queryWithRequestOptsShouldReturnValidQueryResponse() {
    stubResponse(
      url = "/fga/v1/query",
      params = mapOf("q" to equalTo("select role where user:tony-stark is member")),
      responseBody = """{
        "data": [
          {
            "resource_type": "role",
            "resource_id": "admin",
            "relation": "member",
            "warrant": {
              "resource_type": "role",
              "resource_id": "admin",
              "relation": "member",
              "subject": {
                "resource_type": "user",
                "resource_id": "tony-stark"
              }
            },
            "is_implicit": false,
            "meta": {
              "description": "Admin role"
            }
          }
        ],
        "list_metadata": {
          "before": null,
          "after": null
        }
      }""",
      requestHeaders = mapOf("Warrant-Token" to "my-token")
    )

    val options = QueryOptionsBuilder("select role where user:tony-stark is member").build()
    val requestOptions = QueryRequestOptions("my-token")
    val queryResponse = workos.fga.query(options, requestOptions)

    assertEquals(1, queryResponse.data.size)
    assertEquals("role", queryResponse.data[0].resourceType)
    assertEquals("admin", queryResponse.data[0].resourceId)
    assertEquals("member", queryResponse.data[0].relation)
    assertEquals(Warrant("role", "admin", "member", Subject("user", "tony-stark")), queryResponse.data[0].warrant)
    assertEquals(false, queryResponse.data[0].isImplicit)
    assertNotNull(queryResponse.data[0].meta)
    assertEquals("Admin role", queryResponse.data[0].meta?.get("description"))
    assertNull(queryResponse.listMetadata.before)
    assertNull(queryResponse.listMetadata.after)
  }

  @Test
  fun checkResponseShouldDeserializeWarnings() {
    stubResponse(
      "/fga/v1/check",
      """{
        "result": "authorized",
        "is_implicit": false,
        "warnings": [
          {
            "code": "missing_context_keys",
            "message": "Missing context keys",
            "keys": ["key1", "key2"]
          },
          {
            "code": "unknown_warning",
            "message": "Some unknown warning"
          }
        ]
      }"""
    )

    val options = CheckOptionsBuilder(
      listOf(
        WarrantCheckOptions(
          "user",
          "user_123",
          "viewer",
          Subject("user", "user_456")
        )
      )
    ).build()

    val response = workos.fga.check(options)
    
    assertNotNull(response.warnings)
    assertEquals(2, response.warnings?.size)
    
    val missingKeysWarning = response.warnings?.first()
    assertTrue(missingKeysWarning is MissingContextKeysWarning)
    assertEquals("missing_context_keys", missingKeysWarning?.code)
    assertEquals("Missing context keys", missingKeysWarning?.message)
    assertEquals(listOf("key1", "key2"), (missingKeysWarning as MissingContextKeysWarning).keys)
    
    val baseWarning = response.warnings?.get(1)
    assertTrue(baseWarning is Warning)
    assertFalse(baseWarning is MissingContextKeysWarning)
    assertEquals("unknown_warning", baseWarning?.code)
    assertEquals("Some unknown warning", baseWarning?.message)
  }

  @Test
  fun queryResponseShouldDeserializeWarnings() {
    stubResponse(
      "/fga/v1/query",
      """{
        "data": [],
        "list_metadata": {
          "limit": 10,
          "before": null,
          "after": null
        },
        "warnings": [
          {
            "code": "missing_context_keys",
            "message": "Missing context keys",
            "keys": ["key1", "key2"]
          },
          {
            "code": "unknown_warning",
            "message": "Some unknown warning"
          }
        ]
      }"""
    )

    val options = QueryOptionsBuilder("user:user_123@viewer").build()
    val response = workos.fga.query(options)
    
    assertNotNull(response.warnings)
    assertEquals(2, response.warnings?.size)
    
    val missingKeysWarning = response.warnings?.first()
    assertTrue(missingKeysWarning is MissingContextKeysWarning)
    assertEquals("missing_context_keys", missingKeysWarning?.code)
    assertEquals("Missing context keys", missingKeysWarning?.message)
    assertEquals(listOf("key1", "key2"), (missingKeysWarning as MissingContextKeysWarning).keys)
    
    val baseWarning = response.warnings?.get(1)
    assertTrue(baseWarning is Warning)
    assertFalse(baseWarning is MissingContextKeysWarning)
    assertEquals("unknown_warning", baseWarning?.code)
    assertEquals("Some unknown warning", baseWarning?.message)
  }
}
