package org.highjack.scalapipeline.scalaThreads

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, RunnableGraph, Sink, Source}
import org.highjack.scalapipeline.akka.AkkaRestServer
import org.highjack.scalapipeline.pipeline.trigger.TriggerElement
import org.highjack.scalapipeline.pipeline.trigger.TriggerTypeEnum._
import org.highjack.scalapipeline.utils.PerfUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
  * Created by High Jack on 28/10/2018.
  */
case class TriggerToFutureSource(el:TriggerElement) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def trigger(runnable: RunnableGraph[Any]): Source[Any, _] ={
        el.ttype match {
            case FROM_REST_ENDPOINT => {

                AkkaRestServer.exposeTrigger("todo", el.name, el.outputEndpointURL.get, runnable)
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
