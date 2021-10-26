package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Email
    @JsonCreator constructor(
        @JsonProperty("primary")
        var primary: Boolean,
        @JsonProperty("type")
        var type: String,
        @JsonProperty("value")
        var value: String,
)