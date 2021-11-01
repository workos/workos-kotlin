package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

enum class DirectoryType(@JsonValue val type: String) {
  AzureSCIMV2_0("azure scim v2.0"),
  BambooHr("bamboohr"),
  FourthHr("fourth hr"),
  GenericSCIMV1_1("generic scim v1.1"),
  GenericSCIMV2_0("generic scim v2.0"),
  GSuiteDirectory("gsuite directory"),
  Gusto("gusto"),
  JumpCloudSCIM2_0("jump cloud scim v2.0"),
  OktaSCIMV1_1("okta scim v1.1"),
  OktaSCIMV2_0("okta scim v2.0"),
  Rippling("rippling"),
  Workday("workday"),
}
