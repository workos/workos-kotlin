package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.fga.CHECK_RESULT_AUTHORIZED
import com.workos.fga.types.WarrantCheckOptions

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckResponse @JsonCreator constructor(
  @JsonProperty("result")
  val result: String,

  @JsonProperty("is_implicit")
  val isImplicit: Boolean,

  @JsonProperty("debug_info")
  val debugInfo: DebugInfo? = null
) {
  fun authorized(): Boolean {
    return this.result == CHECK_RESULT_AUTHORIZED
  }
}

data class DebugInfo @JsonCreator constructor(
  @JsonProperty("processing_time")
  val processingTime: Int,

  @JsonProperty("decision_tree")
  val decisionTree: DecisionTreeNode
)

data class DecisionTreeNode @JsonCreator constructor(
  @JsonProperty("check")
  val check: WarrantCheckOptions,

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("policy")
  val policy: String? = null,

  @JsonProperty("decision")
  val decision: String,

  @JsonProperty("processing_time")
  val processingTime: Int,

  @JsonProperty("children")
  val children: List<DecisionTreeNode>? = null
)
