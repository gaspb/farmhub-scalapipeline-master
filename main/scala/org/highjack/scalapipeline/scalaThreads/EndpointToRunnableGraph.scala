package org.highjack.scalapipeline.scalaThreads

import akka.actor.{Actor, ActorSystem}
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
import org.reactivestreams.{Publisher, Subscriber}
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
                JSONRestEndpointMethods.TEXT_DOCUMENT_URL_TO_RUNNABLE(el.address.get, el.port.get, flow)

            }
            case AKKA_HTTP_STREAM_LISTEN => {
                HTTPManager.bindHttpPortToLogicFlow(el.port.get, flow)
            }
            case AKKA_REMOTE_STREAM => {
                TCPManager.registerHostFlow(el.address.get, el.port.get, flow.asInstanceOf)
            }
            case KAFKA_COMITTABLE_SUBSCRIBE => { //TODO
                KafkaUtil.subscribeToConsumerAndExecute(  KafkaUtil.createConsumer(), el.kafkaInputTopic, flow)
        }
            case KAFKA_SUBSCRIBE => {
                logger.info("KAFKA_SUBSCRIBE : sourceActor is defined "+(ConsumerService.sourceActor!=null))
                ConsumerService.handler =
                    (s:String)=>
                        Source.single(ByteString(s))
                            .via(flow)
                            .to(Sink.onComplete( s => PerfUtil.stopAndLog())).run()

                    Source.single(ByteString(new Array[Int](5).toString)).via(flow).to(Sink.onComplete( s => PerfUtil.stopAndLog()))
            }

            case MOCK_TCP => {
             new MockMain2().fakeStringByteSource().via(flow).async
                 .to(Sink.onComplete( s => PerfUtil.stopAndLog()))
            }

        }
    }
}


    class KSource extends Actor {
        val log : Logger = LoggerFactory.getLogger(this.getClass)
        def receive = {
            case s: Any =>
                log.info("In KSource actor : "+s)
                val reply = ByteString(s.toString)
                sender() ! reply
        }
    }
