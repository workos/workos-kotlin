// @oagen-ignore-file
package com.workos.common.http

/**
 * Tri-state wrapper for nullable fields on PATCH request bodies.
 *
 * PATCH semantics require distinguishing three states:
 *  - **Absent**: the field was not mentioned — the server preserves its
 *    current value.
 *  - **Present(value)**: the field is explicitly set to `value`.
 *  - **Present(null)**: the field is explicitly cleared.
 *
 * Without this wrapper, a plain `T? = null` parameter cannot distinguish
 * between "not provided" and "clear the value", because both map to the
 * same Kotlin `null`.
 *
 * Usage from Kotlin:
 * ```kotlin
 * workos.organizations.updateOrganization(
 *   id = "org_123",
 *   name = PatchField.of("New Name"),          // set
 *   externalId = PatchField.ofNull(),           // clear
 *   // description is PatchField.Absent by default — omitted from request
 * )
 * ```
 *
 * Usage from Java:
 * ```java
 * workos.getOrganizations().updateOrganization(
 *   "org_123",
 *   PatchField.of("New Name"),
 *   PatchField.ofNull(),
 *   PatchField.absent()
 * );
 * ```
 */
sealed class PatchField<out T> {
  /** The field was not specified and should be omitted from the request body. */
  object Absent : PatchField<Nothing>() {
    override fun toString(): String = "PatchField.Absent"
  }

  /** The field is explicitly set (including an explicit `null` to clear it). */
  data class Present<T>(val value: T?) : PatchField<T>()

  companion object {
    /** Wrap a non-null value. */
    @JvmStatic
    fun <T> of(value: T): PatchField<T> = Present(value)

    /** Explicitly clear the field (sends `null` on the wire). */
    @JvmStatic
    fun <T> ofNull(): PatchField<T> = Present(null)

    /** Leave the field unchanged (omit from request body). */
    @JvmStatic
    fun <T> absent(): PatchField<T> = Absent
  }
}
