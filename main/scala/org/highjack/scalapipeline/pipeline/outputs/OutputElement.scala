package org.highjack.scalapipeline.pipeline.outputs


import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum.OutputTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}


 case class OutputElement(position:Int, name:String, outputEndpointURL : Option[String], otype : OutputTypeEnum) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.OUTPUT
}
