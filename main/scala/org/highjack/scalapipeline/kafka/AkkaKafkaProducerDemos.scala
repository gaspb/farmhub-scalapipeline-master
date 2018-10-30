package org.highjack.scalapipeline.kafka

import java.util

import akka.Done
import akka.kafka.ProducerMessage
import akka.kafka.ProducerMessage.MultiResultPart
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import sample.scaladsl.AkkaKafkaProducer

import scala.concurrent.Future

class AkkaKafkaProducerDemos {

}

object PlainSinkExample extends AkkaKafkaProducer {
    def main(args: Array[String]): Unit = {
        // #plainSink
        val done: Future[Done] =
            Source(1 to 100)
                .map(_.toString)
                .map(value => new ProducerRecord[String, String]("topic1", value))
                .runWith(Producer.plainSink(producerSettings))
        // #plainSink

        terminateWhenDone(done)
    }
}

object PlainSinkWithAkkaKafkaProducer extends AkkaKafkaProducer {
    def main(args: Array[String]): Unit = {
        // #plainSinkWithProducer
        val done = Source(1 to 100)
            .map(_.toString)
            .map(value => new ProducerRecord[String, String]("topic1", value))
            .runWith(Producer.plainSink(producerSettings, kafkaProducer))
        // #plainSinkWithProducer

        terminateWhenDone(done)
    }
}

object ObserveMetricsExample extends AkkaKafkaProducer {
    def main(args: Array[String]): Unit = {
        // format:off
        // #producerMetrics
        val metrics: util.Map[org.apache.kafka.common.MetricName, _ <: org.apache.kafka.common.Metric] =
        kafkaProducer.metrics() // observe metrics
        // #producerMetrics
        // format:on
        metrics.clear()
    }
}

object ProducerFlowExample extends AkkaKafkaProducer {

    def createMessage[KeyType, ValueType, PassThroughType](key: KeyType, value: ValueType, passThrough: PassThroughType) =
    // #singleMessage
        new ProducerMessage.Message[KeyType, ValueType, PassThroughType](
            new ProducerRecord("topicName", key, value),
            passThrough
        )
    // #singleMessage

    def createMultiMessage[KeyType, ValueType, PassThroughType](key: KeyType,
                                                                value: ValueType,
                                                                passThrough: PassThroughType) = {
        import scala.collection.immutable
        // #multiMessage
        new ProducerMessage.MultiMessage[KeyType, ValueType, PassThroughType](
            immutable.Seq(
                new ProducerRecord("topicName", key, value),
                new ProducerRecord("anotherTopic", key, value)
            ),
            passThrough
        )
        // #multiMessage
    }

    def createPassThroughMessage[KeyType, ValueType, PassThroughType](key: KeyType,
                                                                      value: ValueType,
                                                                      passThrough: PassThroughType) =
    // format:off
    // #passThroughMessage
        new ProducerMessage.PassThroughMessage(
            passThrough
        )
    // #passThroughMessage
    // format:on

    def main(args: Array[String]): Unit = {
        // format:off
        // #flow
        val done = Source(1 to 100)
            .map { number =>
                val partition = 0
                val value = number.toString
                ProducerMessage.Message(
                    new ProducerRecord("topic1", partition, "key", value),
                    number
                )
            }
            .via(Producer.flexiFlow(producerSettings))
            .map {
                case ProducerMessage.Result(metadata, message) =>
                    val record = message.record
                    s"${metadata.topic}/${metadata.partition} ${metadata.offset}: ${record.value}"

                case ProducerMessage.MultiResult(parts, passThrough) =>
                    parts
                        .map {
                            case MultiResultPart(metadata, record) =>
                                s"${metadata.topic}/${metadata.partition} ${metadata.offset}: ${record.value}"
                        }
                        .mkString(", ")

                case ProducerMessage.PassThroughResult(passThrough) =>
                    s"passed through"
            }
            .runWith(Sink.foreach(println(_)))
        // #flow
        // format:on

        terminateWhenDone(done)
    }
}
