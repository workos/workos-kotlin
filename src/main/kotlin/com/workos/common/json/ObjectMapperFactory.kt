// @oagen-ignore-file
package com.workos.common.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder

/**
 * Builds the shared Jackson [ObjectMapper] used by the SDK.
 *
 *  - Kotlin module enables data-class deserialization.
 *  - `FAIL_ON_UNKNOWN_PROPERTIES=false` keeps the client forward-compatible
 *    when the API adds fields.
 *  - `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE` lets generated enums fall
 *    back to an `Unknown` sentinel when the API introduces new variants.
 *  - JSR-310 module handles [java.time.OffsetDateTime] serialization without
 *    converting to epoch milliseconds.
 */
object ObjectMapperFactory {
  /** Cached default instance. Thread-safe because ObjectMapper is immutable once configured. */
  @JvmField
  val default: ObjectMapper = create()

  /** Create a new, fully-configured [ObjectMapper] instance. */
  @JvmStatic
  fun create(): ObjectMapper =
    jacksonMapperBuilder()
      .addModule(JavaTimeModule())
      .addModule(UnknownEnumGuardModule.create())
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build()
}
