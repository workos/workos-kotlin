package com.workos.common.models

interface BaseOrganizationMembership {
  val id: String
  val userId: String
  val organizationId: String
  val createdAt: String
  val updatedAt: String
}
