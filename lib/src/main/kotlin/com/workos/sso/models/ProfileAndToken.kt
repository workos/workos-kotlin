package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator

data class ProfileAndToken
@JsonCreator constructor(
    val profile: Profile,

    val token: String,
)
