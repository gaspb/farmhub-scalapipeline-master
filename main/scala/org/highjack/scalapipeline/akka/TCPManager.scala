package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, OutgoingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Framing, RunnableGraph, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.Future


object TCPManager {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    def outputToTCPFlow(host:String, port:Int): Flow[ByteString, ByteString, Future[OutgoingConnection]] = {
        val repl = Flow[ByteString]
            .via(Framing.delimiter(
                ByteString("\n"),
                maximumFrameLength = 256,
                allowTruncation = true))
        val connection : Flow[ByteString, ByteString, Future[OutgoingConnection]] = Tcp().outgoingConnection(host, port).via(repl)

        connection
    }



    def bindTcpPortToLogicFlow(port:Int, flow:Flow[ByteString, _, _]) : RunnableGraph[Any]  = {
        val host = "127.0.0.1"
   /*     val connections: Source[IncomingConnection, Future[ServerBinding]] =*/
//sink.ignore ?
        Tcp().bind(host, port).to(Sink.foreach {connection ⇒ {
            println(s"New connection from: ${connection.remoteAddress}")

            val echo = Flow[ByteString]
                .via(Framing.delimiter(
                    ByteString("\n"), //newLine char as delimiter
                    maximumFrameLength = 256,
                    allowTruncation = true))
                  .via(flow)
                  .map(_ + "\n")//newLine char as delimiter
                  .map(ByteString(_))

            connection.handleWith(echo)
        }})/*.map { b ⇒
            b.unbind() onComplete {
                case _ ⇒ // ...
            }
        }*/
    }
}
