package org.highjack.scalapipeline.pipeline.transformations

import akka.stream.scaladsl.Source
import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}
import org.highjack.scalapipeline.interfaces.GenericTransformation


case class StringSourceToMapTransformationElement(name:String,  position : Int, exposed : Boolean, _origin : Int, _transformation:GenericTransformation[String,Map[String,Long]]) extends PipelineElement {
    def run(rawData: Source[String, _], args: Option[Any]): Source[Map[String,Long], _] = _transformation.run(rawData,args)
    def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.TRANSFORMATION

}
