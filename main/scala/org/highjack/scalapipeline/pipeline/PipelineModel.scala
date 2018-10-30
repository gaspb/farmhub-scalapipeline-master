package org.highjack.scalapipeline.pipeline

import org.highjack.scalapipeline.pipeline.endpoints.EndpointElement
import org.highjack.scalapipeline.pipeline.trigger.TriggerElement

case class PipelineModel(branches : Set[PipelineBranch], pipelineId:String, endpoint:EndpointElement, trigger:TriggerElement)

//endpoint is considered as elem 0:0 (elem 0 of branch 0)
