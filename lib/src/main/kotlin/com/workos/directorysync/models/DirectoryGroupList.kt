package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class DirectoryGroupList
@JsonCreator constructor(
  var data: Array<Group>,
  var listMetadata: ListMetadata
)
