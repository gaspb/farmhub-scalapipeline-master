package org.highjack.scalapipeline.pipeline.outputs

import akka.stream.scaladsl.Source
import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum.OutputTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}
import org.highjack.scalapipeline.scalaThreads.ScalaThread

import scala.concurrent.Future


 case class OutputElement(name:String, outputEndpointURL : String, otype : OutputTypeEnum) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.OUTPUT



}
