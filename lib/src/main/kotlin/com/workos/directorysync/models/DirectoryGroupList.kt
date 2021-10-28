package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class DirectoryGroupList
@JsonCreator constructor(
  val data: Array<Group>,

  val listMetadata: ListMetadata
)
