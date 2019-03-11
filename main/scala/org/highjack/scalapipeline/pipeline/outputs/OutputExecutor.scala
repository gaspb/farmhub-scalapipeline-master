package org.highjack.scalapipeline.pipeline.outputs

import akka.stream.scaladsl.{Flow, Source}
import org.highjack.scalapipeline.akka.AkkaStreamLocalContext._
import org.highjack.scalapipeline.pipeline.PipelineElementExecutor
import org.slf4j.{Logger, LoggerFactory}


@deprecated
case class OutputExecutor(outputElement: OutputElement)(implicit val wrapKafka:Boolean) extends PipelineElementExecutor {

    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    /**
      * Execute a source.via(flow)
      * @param source
      * @param func to apply
      * @return
      */
    def exec[A,B](source:Source[A,_], func: A => B) : Source[B,_] = {
        val flow :Flow[A,B,_] = Flow.fromFunction(a => kafkaSafe(a, func) )
        source.via(flow)
    }


  /*  def run[A<:AnyRef,B](source:Source[A,_], thread:ScalaThread): Source[B,_] = {

    }*/

}

