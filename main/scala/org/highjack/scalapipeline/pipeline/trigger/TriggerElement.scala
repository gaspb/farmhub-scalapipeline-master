package org.highjack.scalapipeline.pipeline.trigger

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.trigger.TriggerTypeEnum.TriggerTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}


case class TriggerElement(name:String, outputEndpointURL : Option[String], ttype: TriggerTypeEnum) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.OUTPUT
    override def position:Int = -1

}
