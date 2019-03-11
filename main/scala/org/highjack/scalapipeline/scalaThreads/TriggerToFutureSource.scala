package org.highjack.scalapipeline.scalaThreads

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, RunnableGraph, Sink, Source}
import org.highjack.scalapipeline.akka.AkkaRestServer
import org.highjack.scalapipeline.akka.AkkaStreamLocalContext._
import org.highjack.scalapipeline.pipeline.trigger.TriggerElement
import org.highjack.scalapipeline.pipeline.trigger.TriggerTypeEnum._
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class TriggerToFutureSource(el:TriggerElement) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def trigger(runnable: RunnableGraph[Any]): Source[Any, _] ={
        el.ttype match {
            case FROM_REST_ENDPOINT => {
                AkkaRestServer.exposeTrigger("trg_", el.name, el.outputEndpointURL.get, runnable)
                Source.empty
            }
            case SHEDULED => {//TODO
                Source.fromFuture[String]({
                    Future {
                        Thread.sleep(30000)
                        ""
                    }
                })
            }
            case SIMPLE_RUN => {
                Source.fromFuture[Any]({
                    Future.successful()
                })
            }
            case WHILE => {
                ???
            }
        }
    }
}
