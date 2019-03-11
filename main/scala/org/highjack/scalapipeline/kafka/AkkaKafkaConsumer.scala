package org.highjack.scalapipeline.kafka


import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.typesafe.config.Config
import org.highjack.scalapipeline.akka.AkkaStreamLocalContext._

trait AkkaKafkaConsumer {


    val maxPartitions = 100
    val config: Config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings : ConsumerSettings[String, Array[Byte]] =
        ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
            .withBootstrapServers("localhost:9092")//TODO
            .withGroupId("group1")//TODO
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val consumerSettingsWithAutoCommit : ConsumerSettings[String, Array[Byte]]  =
    // settings autocommit
        consumerSettings
            .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")

    val producerSettings : ProducerSettings[String, Array[Byte]]  = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
        .withBootstrapServers("localhost:9092")//TODO

    def business[T] : Flow[T,T,NotUsed] = Flow[T]
    def terminateWhenDone(result: Future[Done]): Unit =
        result.onComplete {
            case Failure(e) =>
                system.log.error(e, e.getMessage)
                system.terminate()
            case Success(_) => system.terminate()
        }
}
