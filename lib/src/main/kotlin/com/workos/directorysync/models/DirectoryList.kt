package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class DirectoryList
@JsonCreator constructor(
  val data: Array<Directory>,

  val listMetadata: ListMetadata
)
