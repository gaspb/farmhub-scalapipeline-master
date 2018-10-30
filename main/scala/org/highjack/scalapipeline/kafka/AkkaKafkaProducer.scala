/*
 * Copyright (C) 2014 - 2016 Softwaremill <http://softwaremill.com>
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.scaladsl

import java.util

import akka.actor.ActorSystem
import akka.kafka.ProducerMessage
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.{ExecutionContext, Future}
import akka.Done
import akka.kafka.ProducerMessage.MultiResultPart
import com.typesafe.config.Config

import scala.util.{Failure, Success}

trait AkkaKafkaProducer {
    implicit val system = ActorSystem()
    implicit val materializer : ActorMaterializer = ActorMaterializer()
    // #producer
    // #settings
    val config : Config = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings : ProducerSettings[String, String]  =
        ProducerSettings(config, new StringSerializer, new StringSerializer)
            .withBootstrapServers("localhost:9092")//TODO
    // #settings
    val kafkaProducer : KafkaProducer[String, String] = producerSettings.createKafkaProducer()
    // #producer

    implicit val ec : ExecutionContext= system.dispatcher

    def terminateWhenDone(result: Future[Done]): Unit =
        result.onComplete {
            case Failure(e) =>
                system.log.error(e, e.getMessage)
                system.terminate()
            case Success(_) => system.terminate()
        }
}

