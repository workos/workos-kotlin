// @oagen-ignore-file
package com.workos.common.json

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier

/**
 * Jackson module that guards against accidentally serializing the synthetic
 * `Unknown` enum sentinel.  `Unknown` exists only for forward-compatible
 * *deserialization* of unrecognized API variants.  Sending the literal wire
 * value `"unknown"` to the API will be rejected, so this module surfaces
 * the mistake early by throwing [IllegalArgumentException].
 *
 * The module hooks into the serializer chain via [BeanSerializerModifier].
 * For every `enum class` that declares a `@JsonEnumDefaultValue` member, it
 * wraps the default serializer so that serializing the default member throws
 * instead of emitting its wire value.
 */
object UnknownEnumGuardModule {
  /** Build a [SimpleModule] that throws on serialization of `@JsonEnumDefaultValue` members. */
  fun create(): SimpleModule {
    val module = SimpleModule("UnknownEnumGuard")
    module.setSerializerModifier(
      object : BeanSerializerModifier() {
        override fun modifySerializer(
          config: SerializationConfig,
          type: BeanDescription,
          serializer: JsonSerializer<*>
        ): JsonSerializer<*> {
          val enumClass = type.beanClass
          if (!enumClass.isEnum) return serializer
          val defaultMember =
            enumClass.enumConstants?.filterIsInstance<Enum<*>>()?.firstOrNull { constant ->
              try {
                enumClass.getField(constant.name).isAnnotationPresent(JsonEnumDefaultValue::class.java)
              } catch (_: NoSuchFieldException) {
                false
              }
            }
          if (defaultMember == null) return serializer
          @Suppress("UNCHECKED_CAST")
          return GuardingSerializer(serializer as JsonSerializer<Any>, defaultMember)
        }
      }
    )
    return module
  }

  private class GuardingSerializer(
    private val delegate: JsonSerializer<Any>,
    private val defaultMember: Enum<*>
  ) : JsonSerializer<Any>() {
    override fun serialize(
      value: Any,
      gen: JsonGenerator,
      serializers: SerializerProvider
    ) {
      if (value == defaultMember) {
        throw IllegalArgumentException(
          "Cannot serialize ${defaultMember.declaringJavaClass.simpleName}.${defaultMember.name} — " +
            "the ${defaultMember.name} sentinel is only valid for deserialization of " +
            "unrecognized API values. Use a concrete enum variant instead."
        )
      }
      delegate.serialize(value, gen, serializers)
    }
  }
}
