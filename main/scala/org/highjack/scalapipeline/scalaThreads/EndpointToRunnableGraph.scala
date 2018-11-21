package org.highjack.scalapipeline.scalaThreads

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, SourceQueueWithComplete}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.{HTTPManager, TCPManager}
import org.highjack.scalapipeline.kafka.KafkaUtil
import org.highjack.scalapipeline.mock.{MockMain, MockMain2}
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, JSONRestEndpointMethods}
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum._
import org.highjack.scalapipeline.utils.PerfUtil
import org.highjack.scalapipeline.web.rest.kafka.ConsumerService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.messaging.{Message, MessageHandler}

import scala.concurrent.Future


case class EndpointToRunnableGraph(el:EndpointElement, flow:Flow[ByteString,_,_]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val logger : Logger = LoggerFactory.getLogger(this.getClass)


    def get(): RunnableGraph[Any] = {
        el.endpointType match {
            case AKKA_HTTP_BYTESTRING_READ => {
                JSONRestEndpointMethods.TEXT_DOCUMENT_URL_TO_RUNNABLE(el.address, el.port, flow)

            }
            case AKKA_HTTP_STREAM_LISTEN => {
                HTTPManager.bindHttpPortToLogicFlow(el.port, flow)
            }
            case AKKA_REMOTE_STREAM => {
                TCPManager.registerHostFlow(el.address, el.port, flow.asInstanceOf)
            }
            case KAFKA_COMITTABLE_SUBSCRIBE => { //TODO
                KafkaUtil.subscribeToConsumerAndExecute(  KafkaUtil.createConsumer(), el.kafkaInputTopic, flow)
        }
            case KAFKA_SUBSCRIBE => {

        /* KafkaUtil.subscribeToConsumerAndExecute(  KafkaUtil.createConsumer(), "scala-ms-in", flow)*/
             ConsumerService.activeSource
                 .map(s=>ByteString(s.toString))
                 .via(flow).async
                 .to(Sink.onComplete( s => PerfUtil.stopAndLog()))

            }

            case MOCK_TCP => {
             new MockMain2().fakeStringByteSource().via(flow).async
                 .to(Sink.onComplete( s => PerfUtil.stopAndLog()))
            }

        }
    }
}

