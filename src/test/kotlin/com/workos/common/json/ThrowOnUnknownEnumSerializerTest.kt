// @oagen-ignore-file
package com.workos.common.json

import com.fasterxml.jackson.databind.JsonMappingException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ThrowOnUnknownEnumSerializerTest {
  private val mapper = ObjectMapperFactory.create()

  @Test
  fun `serializing Unknown throws JsonMappingException`() {
    val unknown = com.workos.types.OrganizationDomainDataState.Unknown
    val ex =
      assertThrows(JsonMappingException::class.java) {
        mapper.writeValueAsString(unknown)
      }
    assertTrue(ex.cause is IllegalArgumentException)
    assertTrue(ex.cause!!.message!!.contains("Unknown"))
  }

  @Test
  fun `serializing a concrete variant succeeds`() {
    val concrete =
      com.workos.types.OrganizationDomainDataState.values().first {
        it != com.workos.types.OrganizationDomainDataState.Unknown
      }
    assertDoesNotThrow {
      mapper.writeValueAsString(concrete)
    }
  }
}
