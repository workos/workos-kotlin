package com.workos

import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class WorkOSCachingTest {
  @Test
  fun `service accessors return the same instance on repeated access`() {
    val workos = WorkOS(apiKey = "sk_test_xxx")

    assertSame(workos.organizations, workos.organizations)
    assertSame(workos.userManagement, workos.userManagement)
  }

  @Test
  fun `separate WorkOS instances have independent service instances`() {
    val a = WorkOS(apiKey = "sk_test_a")
    val b = WorkOS(apiKey = "sk_test_b")

    assertNotSame(a.organizations, b.organizations)
    assertNotSame(a.userManagement, b.userManagement)
  }
}
