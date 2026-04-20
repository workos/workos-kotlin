// @oagen-ignore-file
package com.workos.common.http

/**
 * Build a request body map from key-value pairs, dropping entries whose
 * value is `null`. This reduces the repeated `if (x != null) body["x"] = x`
 * pattern in generated service methods.
 *
 * ```kotlin
 * val body = bodyOf(
 *   "name" to name,           // always present (required)
 *   "email" to email,         // null → omitted
 *   "role" to role?.value,    // null → omitted
 * )
 * ```
 */
fun bodyOf(vararg entries: Pair<String, Any?>): LinkedHashMap<String, Any?> {
  val map = linkedMapOf<String, Any?>()
  for ((key, value) in entries) {
    if (value != null) map[key] = value
  }
  return map
}

/**
 * Build a request body map that explicitly includes `null` values for
 * [PatchField.Present] entries while omitting [PatchField.Absent] entries.
 * Non-PatchField values are treated as always-present.
 */
fun patchBodyOf(vararg entries: Pair<String, Any?>): LinkedHashMap<String, Any?> {
  val map = linkedMapOf<String, Any?>()
  for ((key, value) in entries) {
    when (value) {
      is PatchField.Absent -> { /* omit */ }
      is PatchField.Present<*> -> map[key] = value.value
      null -> { /* omit null non-PatchField entries */ }
      else -> map[key] = value
    }
  }
  return map
}

/** Append a query parameter only when [value] is non-null. */
fun MutableList<Pair<String, String>>.addIfNotNull(
  name: String,
  value: String?
) {
  if (value != null) add(name to value)
}

/** Append repeated query parameters for each serialized entry. */
fun MutableList<Pair<String, String>>.addEach(
  name: String,
  values: Iterable<String>
) {
  for (value in values) add(name to value)
}

/** Append a comma-joined query parameter when [values] is non-null. */
fun MutableList<Pair<String, String>>.addJoinedIfNotNull(
  name: String,
  values: Iterable<String>?
) {
  if (values != null) add(name to values.joinToString(","))
}
