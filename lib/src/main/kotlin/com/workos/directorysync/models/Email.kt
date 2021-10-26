package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Email
@JsonCreator constructor(
    var primary: Boolean,
    var type: String,
    var value: String,
)
