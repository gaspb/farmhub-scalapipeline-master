package org.highjack.scalapipeline.pipeline

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum

class ModelTrainingElement(override val name:String) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.MODEL_TRAINING

    def model : AnyRef = null
    def run:AnyRef = null

}
