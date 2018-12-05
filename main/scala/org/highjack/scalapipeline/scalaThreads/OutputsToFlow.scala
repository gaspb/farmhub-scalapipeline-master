package org.highjack.scalapipeline.scalaThreads

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Flow, Sink}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.{AkkaRestServer, TCPManager}
import org.highjack.scalapipeline.pipeline.outputs.OutputElement
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum._
import org.highjack.scalapipeline.scalaThreads.LogicBuilder.logger
import org.highjack.scalapipeline.web.rest.kafka.{ApiResource, MessageModel, MessageModelWithPayload}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.integration.support.MessageBuilder
import org.springframework.stereotype.Service
/**
  * Created by High Jack on 28/10/2018.
  */
case class OutputsToFlow(el:OutputElement) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def get(): Flow[_,_,NotUsed] ={
        el.otype match {
            case TO_AKKA_REMOTE_TCP => {
                ???

            }
            case TO_DOWNLOADABLE_FILE => {
                val filename = el.outputEndpointURL
                val flow = Flow[Any]
                    .map(s => ByteString(s + "\n"))
                    .alsoTo(FileIO.toPath(Paths.get(filename.get)))
                logger.info("Output to flow : "+filename)
                AkkaRestServer.exposeOutputAkkaStream("todo", el.name, el.outputEndpointURL.get, FileIO.fromPath(Paths.get(filename.get)))

                flow
            }
            case WEBSOCKET => {
                logger.info("OUTPUT IS WEBSOCKET")
                val flow = Flow[Any]
                    .fold(0)((a,b)=>{
                        logger.info("IN WEBSOCKET -"+a+"- WRITING "+b.toString())
                        KafkaProducer.produce(a+"-", b.toString)
                        a+1
                    })
                flow
            }
            case TO_REST_ENDPOINT => {
               ???
            }
            case MOCK_TCP => {
                TCPManager.outputToTCPFlow(TCPManager.gtwHost, TCPManager.gtwPort)
            }


        }

    }




}

object KafkaProducer {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def produce(str:String, load:String): Unit = {
        logger.info("KafkaProducer - producing string "+load)

        ApiResource.getStaticChannel.send(MessageBuilder.withPayload(new MessageModel().setMessage(load)).build)
    }
}
