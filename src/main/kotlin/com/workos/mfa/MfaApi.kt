package com.workos.mfa

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.mfa.models.Challenge
import com.workos.mfa.models.Factor
import com.workos.mfa.models.Sms
import com.workos.mfa.models.Totp
import org.apache.http.client.utils.URIBuilder

class MfaApi(private val workos: WorkOS) {

  /**
   * Parameters for the [enrollFactor] method.
   */
  class EnrollFactorOptions @JvmOverloads constructor(
    type: MfaType,
    issuer: String? = null,
    user: String? = null,
    phoneNumber: String? = null
  ) {
    init {
      if (type.toString == "generic_otp") set("type", type.toString())
      if (type.toString == "totp"){
        set("type", type.toString())
        set("issuer", issuer)
        set("user", user)
        }
      if (type.toString == "sms"){
        set("type", type.toString())
        set("phoneNumber", phoneNumber)
        }
    }
  }

  /**
   * Enrolls a Factor.
   */
  @JvmOverloads
  fun enrollFactor(enrollFactorOptions: EnrollFactorOptions): Factor {
    val config = RequestConfig.builder()
      .data(enrollFactorOptions)
      .build()

    return workos.post("/auth/factors/enroll", Factor::class.java, config)
  }

  /**
   * Parameters for the [challengeFactor] method.
   */
  class ChallengeFactorOptions @JvmOverloads constructor(
    authenticationFactorId: String,
    smsTemplate: String? = null
  ) {
    init {
      if (smsTemplate != null) set("smsTemplate", smsTemplate)
    }
  }

  /**
   * Challenges a Factor.
   */
  @JvmOverloads
  fun challengeFactor(challengeFactorOptions: ChallengeFactorOptions): Challenge {
    val config = RequestConfig.builder()
      .data(challengeFactorOptions)
      .build()

    return workos.post("/auth/factors/challenge", Factor::class.java, config)
  }

  /**
   * Parameters for the [verifyFactor] method.
   */
  class ChallengeFactorOptions @JvmOverloads constructor(
    challengeId: String,
    code: String
  )

  /**
   * Verifies a Factor.
   */
  @JvmOverloads
  fun challengeFactor(verifyFactorOptions: VerifyFactorOptions): VerifyFactorResponse {
    val config = RequestConfig.builder()
      .data(verifyFactorOptions)
      .build()

    return workos.post("/auth/factors/verify", VerifyFactorResponse::class.java, config)
  }

  /**
   * Gets a Factor by id.
   */
  fun deleteFactor(id: String) {
    workos.delete("/auth/factors/$id")
  }


  /**
   * Deletes a Factor by id.
   */
  fun getFactor(id: String) {
    workos.get("/auth/factors/$id")
  }
}