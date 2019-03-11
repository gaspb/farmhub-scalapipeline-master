package org.highjack.scalapipeline.akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

object AkkaStreamLocalContext {
    implicit val system = ActorSystem("ppl-local-thd")
    implicit val materializer = ActorMaterializer()
    implicit val executor : ExecutionContextExecutor = system.dispatcher
}
