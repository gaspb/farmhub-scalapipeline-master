package org.highjack.scalapipeline.pipeline

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum

case class PipelineBranch(position:Int, elements : Set[PipelineElement], branchId:Long, parentBranchId:Long) extends PipelineElement {

    override def elemType : PipelineElementTypeEnum = PipelineElementTypeEnum.BRANCH
    override def name : String = "Branch_"+branchId


}
