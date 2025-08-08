
package com.workos.test.user_management

import com.workos.common.exceptions.OAuthException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test

class OAuthErrorHandlingTest : TestBase() {
  val workos = createWorkOSClient()

  @Test
  fun authenticateWithCodeShouldThrowOAuthExceptionOnInvalidGrant() {
    stubResponse(
      "/user_management/authenticate",
      """
                                {
                                    "error" : "invalid_grant",
                                    "error_description" : "The code 'D01K0EWQ9V6SYP9F5D14QPHBQ8Edd' has expired or is invalid."
                                }
                                """,
      responseStatus = 400,
      requestBody = """{
                                    "client_id": "client_id",
                                    "client_secret": "apiKey",
                                    "grant_type": "authorization_code",
                                    "code": "invalid_code"
                                }"""
    )

    assertThrows(OAuthException::class.java) {
      workos.userManagement.authenticateWithCode(
        "client_id",
        "invalid_code"
      )
    }
  }
}
