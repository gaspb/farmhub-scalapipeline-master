package org.highjack.scalapipeline.pipeline.endpoints

import akka.stream.scaladsl.Source
import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum.EndpointTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}

case class EndpointElement(name:String,  endpointType : EndpointTypeEnum, address : String, port:Int, kafkaInputKey : String, options:Map[String,String]) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.ENDPOINT

    def kafkaInputTopic : String = endpointType.toString // only one topic per event-type, even across multiple nodes in the pipeline (or in any pipeline?)
    def byteThreshold : Long = 5000000
    def msThreshold : Long = 30000



  /*  def run(): Source[_,_] = {
        _endpointType match {
            case EndpointTypeEnum.REST_TEXTFILE_STREAM =>

            case EndpointTypeEnum.REST_JSON_STREAM =>
                JSONRestEndpointMethods._JSON_STREAM_API_URL_TO_JSON_OBJECT_SOURCE_TEST(_address)
        }
    }*/
}
