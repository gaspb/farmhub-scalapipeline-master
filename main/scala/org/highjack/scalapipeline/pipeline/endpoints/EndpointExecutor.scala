package org.highjack.scalapipeline.pipeline.endpoints

import org.highjack.scalapipeline.pipeline.PipelineElementExecutor

@deprecated
case class EndpointExecutor(endpointElement:EndpointElement)
                           (implicit val wrapKafka:Boolean)
                            extends PipelineElementExecutor{

}
