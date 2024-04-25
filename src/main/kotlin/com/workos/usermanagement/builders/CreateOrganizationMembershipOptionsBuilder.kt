package com.workos.usermanagement.builders

import com.workos.usermanagement.types.CreateOrganizationMembershipOptions

/**
 * Builder for options when creating an organization membership.
 *
 * @param userId The id of the user.
 * @param organizationId The id of the organization.
 * @param roleSlug The unique role identifier. Defaults to `member`.
 */
class CreateOrganizationMembershipOptionsBuilder @JvmOverloads constructor(
  private var userId: String,
  private var organizationId: String,
  private var roleSlug: String? = null,
) {
  /**
   * Role Slug
   */
  fun roleSlug(value: String) = apply { roleSlug = value }

  /**
   * Generates the CreateOrganizationMembershipOptions object.
   */
  fun build(): CreateOrganizationMembershipOptions {
    return CreateOrganizationMembershipOptions(
      userId = this.userId,
      organizationId = this.organizationId,
      roleSlug = this.roleSlug,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(userId: String, organizationId: String): CreateOrganizationMembershipOptionsBuilder {
      return CreateOrganizationMembershipOptionsBuilder(userId, organizationId)
    }
  }
}
