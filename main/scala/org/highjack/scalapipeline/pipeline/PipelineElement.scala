package org.highjack.scalapipeline.pipeline

import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum.PipelineElementTypeEnum

trait PipelineElement {
    def elemType : PipelineElementTypeEnum
    def name : String
    def position : Int

/*
    var applyToFlowValue : Boolean = false
    //if true,
*/

    //TODO
    /*
    Get a source from the endpoint/kafka manually,
    the source and an implicit pass(value) method is given
    At each endpoint there is the "commit to kafka" option

    */
}
