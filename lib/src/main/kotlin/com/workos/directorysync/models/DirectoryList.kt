package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator

data class DirectoryList
@JsonCreator constructor(
    var data: Array<Directory>
)
