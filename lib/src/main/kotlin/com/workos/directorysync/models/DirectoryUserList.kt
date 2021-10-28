package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class DirectoryUserList
@JsonCreator constructor(
  var data: Array<User>,
  var listMetadata: ListMetadata
)
