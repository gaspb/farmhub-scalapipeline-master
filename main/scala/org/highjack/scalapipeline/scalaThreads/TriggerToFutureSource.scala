package org.highjack.scalapipeline.scalaThreads

import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import org.highjack.scalapipeline.pipeline.trigger.TriggerElement
import org.highjack.scalapipeline.pipeline.trigger.TriggerTypeEnum._

import scala.concurrent.Future


/**
  * Created by High Jack on 28/10/2018.
  */
case class TriggerToFutureSource(el:TriggerElement) {

    def trigger(): Source[Future[Any], _] ={
        el.ttype match {
            case FROM_REST_ENDPOINT => {
                ???

            }
            case SHEDULED => {
                ???
            }
            case SIMPLE_RUN => {
                Source.single(Future.successful())
            }
            case WHILE => {
                ???
            }


        }

    }




}
