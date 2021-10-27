package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class DirectoryList
@JsonCreator constructor(
    var data: Array<Directory>,
    var listMetadata: ListMetadata
)
