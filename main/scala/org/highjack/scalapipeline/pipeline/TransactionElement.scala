package org.highjack.scalapipeline.pipeline

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum

class TransactionElement(override val position:Int, override val name:String) extends PipelineElement {
    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.TRANSACTION

    def transactionType : AnyRef = null
    def sparkContext : AnyRef = null
    def run:AnyRef = null

}
