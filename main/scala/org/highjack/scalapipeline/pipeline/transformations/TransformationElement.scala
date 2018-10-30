package org.highjack.scalapipeline.pipeline.transformations


import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum
import org.highjack.scalapipeline.pipeline.transformations.TransformationTypeEnum.TransformationTypeEnum
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}

case class TransformationElement(name:String, ttype:TransformationTypeEnum, opt:Map[String, _]) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.TRANSFORMATION


    def sparkContext : AnyRef = null


}
