package org.highjack.scalapipeline.akka

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import AkkaStreamLocalContext._
import akka.stream.scaladsl.{Flow, Framing, RunnableGraph, Sink, Source}
import akka.util.ByteString

object HTTPManager {
    def bindHttpPortToLogicFlow(port:Int, flow:Flow[ByteString, _, _]) : RunnableGraph[Any]  = {
        val host = "127.0.0.1"//TODO
        Http().bind(host, port).to(Sink.foreach {connection ⇒ {
            println(s"New http connection from: ${connection.remoteAddress}")
            val echo = Flow[HttpRequest]
                  .map[Source[ByteString, _]](
                      _.entity
                          .dataBytes
                          .via(
                              Framing.delimiter(
                                ByteString("\n"),
                                maximumFrameLength = 256,
                                allowTruncation = true))
                        .via(flow)
                        .map(_.toString + "\n")
                        .map(ByteString(_))

                  )
                .map(b=>HttpResponse(200, entity = HttpEntity(ContentTypes.`application/octet-stream`, b)))
            connection.handleWith(echo)
        }})
    }
}
