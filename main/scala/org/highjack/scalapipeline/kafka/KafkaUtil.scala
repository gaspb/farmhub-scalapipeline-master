package org.highjack.scalapipeline.kafka

import java.util.{Collections, Properties}

import akka.stream.scaladsl.{Flow,RunnableGraph, Sink, Source}
import akka.util.ByteString
import org.apache.kafka.clients.consumer.{ConsumerRecord,  KafkaConsumer}
import org.highjack.scalapipeline.kafka.KafkaConstants._
import org.highjack.scalapipeline.utils.PerfUtil

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object KafkaUtil {
    def createConsumer() : KafkaConsumer[String, String] = {
        val props = new Properties()
        props.put("bootstrap.servers", KAFKA_BROKER)
        props.put("key.serializer", STRING_SERIALIZER)
        props.put("value.deserializer", STRING_DESERIALIZER)
        new KafkaConsumer[String, String](props)
    }

    def subscribeToConsumerAndExecute(consumer: KafkaConsumer[String, String],
                                      topic:String,
                                      flow:Flow[ByteString,_,_]) : RunnableGraph[_] = {
        implicit def any2iterable[A](a: A) : Iterable[A] = Some(a)
        consumer.subscribe(Collections.singletonList(topic))
       val runnable : RunnableGraph[_] = Source.fromFuture(Future(consumer.poll(1000).records(topic)))
                  .mapConcat[ConsumerRecord[String,String]](t=> t.asScala.to)
                  .map((t:ConsumerRecord[String,String])=>ByteString(t.value()))
          .via(flow)
          .to(Sink.onComplete( s => PerfUtil.stopAndLog()))
        runnable

    }
}
