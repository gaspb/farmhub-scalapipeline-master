package org.highjack.scalapipeline.scalaThreads

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.{HTTPManager, TCPManager}
import org.highjack.scalapipeline.kafka.KafkaUtil
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, JSONRestEndpointMethods}
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum._

import scala.concurrent.Future


case class EndpointToRunnableGraph(el:EndpointElement, flow:Flow[ByteString,_,_]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher



    def get(): RunnableGraph[Any] = {
        el.endpointType match {
            case AKKA_HTTP_BYTESTRING_READ => {
                JSONRestEndpointMethods.TEXT_DOCUMENT_URL_TO_RUNNABLE(el.address, el.port, flow)

            }
            case AKKA_HTTP_STREAM_LISTEN => {
                HTTPManager.bindHttpPortToLogicFlow(el.port, flow)
            }
            case AKKA_REMOTE_STREAM => {
                TCPManager.bindTcpPortToLogicFlow(el.port, flow)
            }
            case KAFKA_COMITTABLE_SUBSCRIBE => { //TODO
                KafkaUtil.subscribeToConsumerAndExecute(  KafkaUtil.createConsumer(), el.kafkaInputTopic, flow)
        }
            case KAFKA_SUBSCRIBE => {
                KafkaUtil.subscribeToConsumerAndExecute(  KafkaUtil.createConsumer(), el.kafkaInputTopic, flow)
            }

        }
    }
}

