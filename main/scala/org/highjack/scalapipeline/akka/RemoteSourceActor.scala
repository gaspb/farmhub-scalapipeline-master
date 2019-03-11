package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.Actor
import akka.stream.{ActorMaterializer, SourceRef}
import akka.pattern.pipe
import akka.stream.scaladsl.{Source, StreamRefs}

import scala.concurrent.Future



@deprecated //handled by AkkaStream
class RemoteSourceActor extends Actor {
    import context.dispatcher
    implicit val mat = ActorMaterializer()(context)
    case class RequestLogs(streamId: Int)
    case class LogsOffer(streamId: Int, sourceRef: SourceRef[String])

    def receive = {
        case RequestLogs(streamId) ⇒
            val source: Source[String, NotUsed] = streamLogs(streamId)
            val ref: Future[SourceRef[String]] = source.runWith(StreamRefs.sourceRef())
            val reply: Future[LogsOffer] = ref.map(LogsOffer(streamId, _))
            reply pipeTo sender()
    }

    def streamLogs(streamId: Long): Source[String, NotUsed] = ???
}

