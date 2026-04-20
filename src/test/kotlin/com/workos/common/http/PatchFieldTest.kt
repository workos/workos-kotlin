// @oagen-ignore-file
package com.workos.common.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchFieldTest {
  @Test
  fun `Absent values are omitted from patchBodyOf`() {
    val body =
      patchBodyOf(
        "name" to PatchField.Absent,
        "external_id" to PatchField.Absent
      )

    assertTrue(body.isEmpty())
  }

  @Test
  fun `Present null values are serialized as JSON null`() {
    val body =
      patchBodyOf(
        "external_id" to PatchField.ofNull<String>()
      )

    assertTrue(body.containsKey("external_id"))
    assertEquals(null, body["external_id"])
  }

  @Test
  fun `Present values are serialized normally`() {
    val body =
      patchBodyOf(
        "name" to PatchField.of("New Name")
      )

    assertEquals("New Name", body["name"])
    assertFalse(body.isEmpty())
  }
}
