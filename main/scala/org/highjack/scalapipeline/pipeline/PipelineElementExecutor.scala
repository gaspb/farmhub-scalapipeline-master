package org.highjack.scalapipeline.pipeline

import akka.kafka.ConsumerMessage
import org.apache.kafka.clients.consumer.ConsumerRecord


trait PipelineElementExecutor {
    implicit val wrapKafka : Boolean

    def unwrap[A](msg:Any): A = {
        if (wrapKafka)
            msg.asInstanceOf[ConsumerMessage.CommittableMessage[String, A]].record.value()
        else msg.asInstanceOf
    }
    def wrap[A, B](value:B, orig:A): Any = {
        if (wrapKafka) {
            val msg = orig.asInstanceOf[ConsumerMessage.CommittableMessage[String, A]]
            ConsumerMessage.CommittableMessage[String, B](
                new ConsumerRecord[String, B](
                    msg.record.topic,
                    msg.record.partition,
                    msg.record.offset,
                    msg.record.key,
                    value),
                msg.committableOffset
            )}
        else value
    }
    def kafkaSafe[A,B](in:A, func:A=>B) :  B = {
        val unwrapped = unwrap(in)
        val out = func(unwrapped)
        wrap[A,B](out, in).asInstanceOf
    }

}
