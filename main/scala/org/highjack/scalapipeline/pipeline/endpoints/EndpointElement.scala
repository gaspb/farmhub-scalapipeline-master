package org.highjack.scalapipeline.pipeline.endpoints

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum.EndpointTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}

case class EndpointElement(name:String,  endpointType : EndpointTypeEnum, address : Option[String], port:Option[Int], kafkaInputKey : Option[String], options:Option[Map[String,String]]) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.ENDPOINT
    def kafkaInputTopic : String = endpointType.toString // only one topic per event-type, even across multiple nodes in the pipeline (or in any pipeline?)
    def byteThreshold : Long = 5000000
    def msThreshold : Long = 30000
    override def position:Int = -1
}
