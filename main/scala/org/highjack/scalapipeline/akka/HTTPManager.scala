package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing, RunnableGraph, Sink, Source, StreamConverters}
import akka.util.ByteString

import scala.concurrent.Future


object HTTPManager {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

   /* def outputToHttpFlow(host:String, port:Int): Flow[HttpResponse, ByteString, Future[OutgoingConnection]] = {
        val repl = Flow[HttpResponse]
        val repl2 = Flow[HttpResponse].map(f=>f.entity.)
          /*  .via(Framing.delimiter(
                ByteString("\n"),
                maximumFrameLength = 256,
                allowTruncation = true))*/
        val connection : Flow[ByteString, ByteString, Future[OutgoingConnection]] = Http().outgoingConnection(host, port).via(repl).via()

        connection
    }
*/


    def bindHttpPortToLogicFlow(port:Int, flow:Flow[ByteString, _, _]) : RunnableGraph[Any]  = {
        val host = "127.0.0.1"
   /*     val connections: Source[IncomingConnection, Future[ServerBinding]] =*/
//sink.ignore ?
        Http().bind(host, port).to(Sink.foreach {connection ⇒ {
            println(s"New http connection from: ${connection.remoteAddress}")


            val echo = Flow[HttpRequest]
                  .map[Source[ByteString, _]](
                      _.entity
                          .dataBytes
                          .via(
                              Framing.delimiter(
                                ByteString("\n"), //newLine char as delimiter
                                maximumFrameLength = 256,
                                allowTruncation = true))
                        .via(flow)
                        .map(_.toString + "\n")//newLine char as delimiter
                        .map(ByteString(_))

                  )
                .map(b=>HttpResponse(200, entity = HttpEntity(ContentTypes.`application/octet-stream`, b)))
            connection.handleWith(echo)
        }})/*.map { b ⇒
            b.unbind() onComplete {
                case _ ⇒ // ...
            }
        }*/
    }
}
