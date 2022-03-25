package com.workos.mfa

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.mfa.models.Challenge
import com.workos.mfa.models.Factor
import com.workos.mfa.models.VerifyFactorResponse

class MfaApi(private val workos: WorkOS) {

  /**
   * Parameters for the [enrollFactor] method.
   */
  @JsonInclude(Include.NON_NULL)
  class EnrollFactorOptions @JvmOverloads constructor(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("issuer")
    val issuer: String? = null,

    @JsonProperty("user")
    val user: String? = null,

    @JsonProperty("phone_number")
    val phoneNumber: String? = null,
  ) {
    /**
    * Builder class for [enrollFactorOptions].
    */
    class EnrollFactorOptionsBuilder {
      private var type: String? = null

      private var issuer: String? = null

      private var user: String? = null

      private var phoneNumber: String? = null

      /**
        * Sets the type.
        */
      fun type(value: String) = apply { type = value }

      /**
        * Sets the totp issuer.
        */
      fun issuer(value: String) = apply { issuer = value }

      /**
        * Sets the totp user.
        */
      fun user(value: String) = apply { user = value }

      /**
        * Sets the totp user.
        */
      fun phoneNumber(value: String) = apply { phoneNumber = value }

      /**
        * Creates a [EnrollFactorOptions] with the given builder parameters.
        */
      fun build(): EnrollFactorOptions {
        if (type == null || (type != "generic_otp" && type != "totp" && type != "sms")) {
          throw IllegalArgumentException("The mfa type must be either generic_otp, totp, or sms")
        }

        if (type == "totp") {
          if (issuer == null || user == null){
            throw IllegalArgumentException("Type totp must have an issuer and user")
          }
        }

        if (type == "sms") {
          if (phoneNumber == null){
            throw IllegalArgumentException("Type sms type must have a phone number")
          }
        }

        return EnrollFactorOptions(
          type = type!!,
          issuer = issuer,
          user = user,
          phoneNumber = phoneNumber
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): EnrollFactorOptionsBuilder {
        return EnrollFactorOptionsBuilder()
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
  )

  /**
   * Challenges a Factor.
   */
  @JvmOverloads
  fun challengeFactor(challengeFactorOptions: ChallengeFactorOptions): Challenge {
    val config = RequestConfig.builder()
      .data(challengeFactorOptions)
      .build()

    return workos.post("/auth/factors/challenge", Challenge::class.java, config)
  }

  /**
   * Parameters for the [verifyFactor] method.
   */
  class VerifyFactorOptions @JvmOverloads constructor(
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
  fun getFactor(id: String): Factor {
    return workos.get("/auth/factors/$id", Factor::class.java)
  }

  /**
   * Deletes a Factor by id.
   */
  fun deleteFactor(id: String) {
    workos.delete("/auth/factors/$id")
  }
}
