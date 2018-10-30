package org.highjack.scalapipeline.pipeline.outputs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.pipeline.PipelineElementExecutor
import org.highjack.scalapipeline.scalaThreads.ScalaThread
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext


case class OutputExecutor(outputElement: OutputElement)(implicit val wrapKafka:Boolean) extends PipelineElementExecutor {

    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    implicit val system = ActorSystem()
    implicit val exec: ExecutionContext =system.dispatcher

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


    def run[A<:AnyRef,B](source:Source[A,_], thread:ScalaThread): Source[B,_] = {
        /*outputElement.elemType match {
            case _ => _ //TODO
        }*/
        ???
    }

}
/*

idleTimeout(idleDuration)
                .scan("")((acc, curr) =>
                    if (acc.contains(jsonDelimiter getOrElse "\r\n")) {logger.error("---------DIVIDING--------"+curr.utf8String);curr.utf8String}
                    else acc + curr.utf8String
                )
                .filterNot(_.trim.isEmpty)
                .map(curr =>  Json.parse(curr))

 */
