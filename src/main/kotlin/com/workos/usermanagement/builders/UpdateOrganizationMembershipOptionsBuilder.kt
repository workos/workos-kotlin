package com.workos.usermanagement.builders

import com.workos.usermanagement.types.UpdateOrganizationMembershipOptions

/**
 * Builder for options when creating an organization membership.
 *
 * @param userId The id of the user.
 * @param organizationId The id of the organization.
 * @param roleSlug The unique role identifier.
 */
class UpdateOrganizationMembershipOptionsBuilder @JvmOverloads constructor(
  private var id: String,
  private var roleSlug: String,
) {
  /**
   * Generates the CreateOrganizationMembershipOptions object.
   */
  fun build(): UpdateOrganizationMembershipOptions {
    return UpdateOrganizationMembershipOptions(
      id = this.id,
      roleSlug = this.roleSlug,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(id: String, roleSlug: String): UpdateOrganizationMembershipOptionsBuilder {
      return UpdateOrganizationMembershipOptionsBuilder(id, roleSlug)
    }
  }
}
