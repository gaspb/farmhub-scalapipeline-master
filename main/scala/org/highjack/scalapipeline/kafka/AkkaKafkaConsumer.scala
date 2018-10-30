package org.highjack.scalapipeline.kafka

import akka.kafka._
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow}
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import com.typesafe.config.Config


trait AkkaKafkaConsumer {
    implicit val system = ActorSystem()
    implicit val ec : ExecutionContext = system.dispatcher
    implicit val materializer : ActorMaterializer = ActorMaterializer()

    val maxPartitions = 100

    // #settings
    val config: Config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings : ConsumerSettings[String, Array[Byte]] =
        ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
            .withBootstrapServers("localhost:9092")//TODO
            .withGroupId("group1")
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //#settings

    val consumerSettingsWithAutoCommit : ConsumerSettings[String, Array[Byte]]  =
    // #settings-autocommit
        consumerSettings
            .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")
    // #settings-autocommit

    val producerSettings : ProducerSettings[String, Array[Byte]]  = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
        .withBootstrapServers("localhost:9092")//TODO

    def business[T] : Flow[T,T,NotUsed] = Flow[T]
   // def businessLogic(record: ConsumerRecord[String, Array[Byte]]): Future[Done] = ???

    def terminateWhenDone(result: Future[Done]): Unit =
        result.onComplete {
            case Failure(e) =>
                system.log.error(e, e.getMessage)
                system.terminate()
            case Success(_) => system.terminate()
        }
}
