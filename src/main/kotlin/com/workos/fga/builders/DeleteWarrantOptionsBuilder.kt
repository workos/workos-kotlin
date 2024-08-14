package com.workos.fga.builders

import com.workos.fga.models.Subject
import com.workos.fga.types.DeleteWarrantOptions

/**
 * Builder for options when performing a warrant delete.
 *
 * @param resourceType The type of the resource. Must be one of your system's existing resource types.
 * @param resourceId The unique ID of the resource.
 * @param relation The relation for this resource to subject association. The relation must be valid per the resource type definition.
 * @param subject (see [Subject]) The resource with the specified `relation` to the resource provided by resourceType and resourceId.
 * @param policy A boolean expression that must evaluate to true for this warrant to apply. The expression can reference variables provided in the context attribute of access check requests.
 */
class DeleteWarrantOptionsBuilder @JvmOverloads constructor(
  private var resourceType: String,
  private var resourceId: String,
  private var relation: String,
  private var subject: Subject,
  private var policy: String? = null,
) {
  fun resourceType(value: String) = apply { resourceType = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun relation(value: String) = apply { relation = value }

  fun subject(value: Subject) = apply { subject = value }

  fun policy(value: String) = apply { policy = value }

  fun build(): DeleteWarrantOptions {
    return DeleteWarrantOptions(
      resourceType = this.resourceType,
      resourceId = this.resourceId,
      relation = this.relation,
      subject = this.subject,
      policy = this.policy,
    )
  }

  companion object {
    @JvmStatic
    fun create(resourceType: String, resourceId: String, relation: String, subject: Subject): DeleteWarrantOptionsBuilder {
      return DeleteWarrantOptionsBuilder(resourceType, resourceId, relation, subject)
    }
  }
}
