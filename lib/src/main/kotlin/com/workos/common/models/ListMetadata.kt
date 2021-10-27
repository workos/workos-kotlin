package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator

data class ListMetadata
@JsonCreator constructor(
    var after: String,
    var before: String
)
