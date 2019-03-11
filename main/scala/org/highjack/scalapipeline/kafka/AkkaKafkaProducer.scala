package org.highjack.scalapipeline.kafka

import akka.kafka.ProducerSettings
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContext, Future}
import akka.Done
import com.typesafe.config.Config
import org.highjack.scalapipeline.akka.AkkaStreamLocalContext.{system, materializer}

import scala.util.{Failure, Success}

trait AkkaKafkaProducer {
    val config : Config = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings : ProducerSettings[String, String]  =
        ProducerSettings(config, new StringSerializer, new StringSerializer)
            .withBootstrapServers("localhost:9092")//TODO

    val kafkaProducer : KafkaProducer[String, String] = producerSettings.createKafkaProducer()
    implicit val ec : ExecutionContext= system.dispatcher

    def terminateWhenDone(result: Future[Done]): Unit =
        result.onComplete {
            case Failure(e) =>
                system.log.error(e, e.getMessage)
                system.terminate()
            case Success(_) => system.terminate()
        }
}

