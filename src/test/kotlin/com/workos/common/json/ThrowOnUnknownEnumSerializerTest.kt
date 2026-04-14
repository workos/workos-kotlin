// @oagen-ignore-file
package com.workos.common.json

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ThrowOnUnknownEnumSerializerTest {
  private val mapper = ObjectMapperFactory.create()

  // Use the first generated enum we can find — OrganizationDomainDataState is
  // guaranteed present and has @JsonEnumDefaultValue + @JsonSerialize on Unknown.
  @Test
  fun `serializing Unknown throws IllegalArgumentException`() {
    val unknown = com.workos.types.OrganizationDomainDataState.Unknown
    val ex =
      assertThrows(IllegalArgumentException::class.java) {
        mapper.writeValueAsString(unknown)
      }
    assertTrue(ex.message!!.contains("Unknown"))
  }

  @Test
  fun `serializing a concrete variant succeeds`() {
    val concrete = com.workos.types.OrganizationDomainDataState.values().first { it != com.workos.types.OrganizationDomainDataState.Unknown }
    assertDoesNotThrow {
      mapper.writeValueAsString(concrete)
    }
  }
}
