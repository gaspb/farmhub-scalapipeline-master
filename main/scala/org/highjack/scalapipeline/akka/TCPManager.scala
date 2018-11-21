package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.io.Tcp.Message
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, OutgoingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Framing, Keep, RunnableGraph, Sink, Source, Tcp}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.{Failure, Success}


object TCPManager {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    final val log: Logger = LoggerFactory.getLogger(this.getClass)
    val gtwHost = "127.0.0.1"//GTW
    val gtwPort = 6001//GTW
    val thisPort = 6002

    val remoteAddressToFlowMap : collection.mutable.ListMap[String, IncomingConnection] = collection.mutable.ListMap.empty[String, IncomingConnection] //TODO use actors instead of flow, allowing to destroy it when pipeline is shutdown


    val connections : Source[IncomingConnection,Future[ServerBinding]] = Tcp().bind("0.0.0.0", thisPort)
    val binding : Future[ServerBinding]  = connections.to(Sink.foreach {connection ⇒ {
        val address = connection.remoteAddress.getAddress.getHostAddress
        val port = connection.remoteAddress.getPort



        val registered = remoteAddressToFlowMap.get(address+port)
        if(registered.isEmpty) { //@Debug TODO DELETE

            log.info(s"An unregistered TCP connection was received from : ${connection.remoteAddress}. cancelling")
            //connection.handleWith(Flow.fromSinkAndSourceCoupled(Sink.cancelled, Source.empty))
            connection.handleWith(Flow.fromSinkAndSourceCoupled(Sink.onComplete(t=>log.info("completed-")), Source.single(ByteString(""))))


        } else {
            log.info(s"New registered TCP connection from: ${connection.remoteAddress}")

          //  connection.handleWith(Flow.fromSinkAndSource(Sink.foreach(f=> {log.info("TCP--- "+f.utf8String);f}), Source.empty))

    }





    }}).run()

    binding onComplete {
        case Success(b) =>
            println("Server started, listening on: " + b.localAddress)
        case Failure(e) =>
            println(s"Server could not bind to 0.0.0.0:$thisPort: ${e.getMessage}")
    }

    def outputToTCPFlow(host:String, port:Int): Flow[ByteString, ByteString, NotUsed] = {
        log.info(s"_outputToTCPFlow ")

        val repl = Flow[ByteString]
            .via(Framing.delimiter(
                ByteString("\n"),
                maximumFrameLength = 256,
                allowTruncation = true))
          .map(l=>{log.info("outputToTCPFlow--"+l.utf8String);l})

       repl.via(Tcp().outgoingConnection(host, port))
    }
    def outputToTCPRunnable(host:String, port:Int, flow:Flow[ByteString,ByteString,_]): RunnableGraph[Future[OutgoingConnection]] = {
        Tcp().outgoingConnection(host, port).join(flow) //TODO
    }


    /**
      * @deprecated
      */
    def registerHostFlow(address:String, port:Int, flow:Flow[ByteString, ByteString, _]) : RunnableGraph[Any]  = {

   /*     val connections: Source[IncomingConnection, Future[ServerBinding]] =*/
//sink.ignore ?
        Tcp().bind(gtwHost, gtwPort).to(Sink.foreach {connection ⇒ {
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

       /* remoteAddressToFlowMap + ((address+port, flow))*/
    }
}
