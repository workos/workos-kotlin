// @oagen-ignore-file
package com.workos.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * Jackson serializer that throws [IllegalArgumentException] when the caller
 * attempts to serialize the synthetic `Unknown` enum sentinel. `Unknown`
 * exists only for forward-compatible *deserialization* — the API will reject
 * the literal wire value `"unknown"` for every enum, so silently sending it
 * masks a programming error.
 *
 * Attach this to the `Unknown` constant via `@JsonSerialize(using = ...)`.
 */
class ThrowOnUnknownEnumSerializer : JsonSerializer<Enum<*>>() {
  override fun serialize(value: Enum<*>, gen: JsonGenerator, serializers: SerializerProvider) {
    throw IllegalArgumentException(
      "Cannot serialize ${value.declaringJavaClass.simpleName}.Unknown — " +
        "the Unknown sentinel is only valid for deserialization of unrecognized " +
        "API values. Use a concrete enum variant instead."
    )
  }
}
