package org.highjack.scalapipeline.pipeline

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerMessage, ProducerMessage, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.highjack.scalapipeline.kafka.AkkaKafkaConsumer
import sample.scaladsl.AkkaKafkaProducer

import scala.concurrent.{ExecutionContext, Future}

object KafkaElementMethods extends AkkaKafkaConsumer {
    override implicit val system = ActorSystem()
    override implicit val materializer : ActorMaterializer = ActorMaterializer()
    override   implicit val ec : ExecutionContext= system.dispatcher
    def KAFKA_COMMITABLE_SUBSCRIBE_IN (sourceTopic:String, key:String) : Source[ConsumerMessage.CommittableMessage[String, Array[Byte]], Consumer.Control]= {
        Consumer
            .committableSource(consumerSettings, Subscriptions.topics(sourceTopic))
            .filter(_.record.key equals key)//TODO
    }

    def KAFKA_COMMITABLE_SUBSCRIBE_OUT(outputTopic:String, source: Source[ConsumerMessage.CommittableMessage[String, Array[Byte]], Consumer.Control]) : Future[Done] = {
        val control :DrainingControl[Done] = source
            .map(
                msg =>
                    ProducerMessage.Message[String, Array[Byte], ConsumerMessage.CommittableOffset](
                        new ProducerRecord(outputTopic, msg.record.value),
                        msg.committableOffset
                    )
            )
            .via(Producer.flexiFlow(producerSettings))
            .map(_.passThrough)
            .batch(max = 20, CommittableOffsetBatch.apply)(_.updated(_))
            .mapAsync(3)(_.commitScaladsl())
            .toMat(Sink.ignore)(Keep.both)
            .mapMaterializedValue(DrainingControl.apply)
            .run()
        control.drainAndShutdown()
    }

}
