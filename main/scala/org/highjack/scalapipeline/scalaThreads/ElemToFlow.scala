package org.highjack.scalapipeline.scalaThreads

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import org.highjack.scalapipeline.pipeline.{PipelineElement, PipelineElementTypeEnum}
import org.highjack.scalapipeline.pipeline.endpoints.EndpointExecutor
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputExecutor}
import org.highjack.scalapipeline.pipeline.transformations.{TransformationElement, TransformationExecutor}
import org.slf4j.{Logger, LoggerFactory}
import org.json4s.DefaultFormats
/**
  * Created by High Jack on 28/10/2018.
  */
case class ElemToFlow(pipelineElement:PipelineElement) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def get(): Flow[Any, Any, NotUsed] = {
        logger.info("elemToFlow of type "+pipelineElement.elemType)
        pipelineElement.elemType match {

            case PipelineElementTypeEnum.TRANSFORMATION => {//AKKA FLOW + SPARK
                TransformationsToFlow(pipelineElement.asInstanceOf[TransformationElement]).get().asInstanceOf[Flow[Any, Any, NotUsed]]
            }
            case PipelineElementTypeEnum.OUTPUT => { //AKKA ask KAFKA (ask SPARK?) ask CASSANDRA feed AKKA
                OutputsToFlow(pipelineElement.asInstanceOf[OutputElement]).get().asInstanceOf[Flow[Any, Any, NotUsed]]
            }

        }

    }

}
