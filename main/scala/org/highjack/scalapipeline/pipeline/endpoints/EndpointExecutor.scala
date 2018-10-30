package org.highjack.scalapipeline.pipeline.endpoints

import java.util.{Collections, Properties}
import java.util.concurrent.Executors

import akka.stream.scaladsl.Source
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.highjack.scalapipeline.kafka.KafkaUtil
import org.highjack.scalapipeline.pipeline.PipelineElementExecutor
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum._
import org.highjack.scalapipeline.utils.Java8Util.toJavaConsumer


case class EndpointExecutor(endpointElement:EndpointElement) (implicit val wrapKafka:Boolean) extends PipelineElementExecutor{


    /**
      *
      * @return the source
      */
  /*  def run() : Source[_,_] = {

        endpointElement._endpointType match {
            case FINITE_JSON_LONG_POLL => {
                Source.empty
            }
            case FINITE_BYTESTRING_LONG_POLL => {
                Source.empty
            }
            case FINITE_JSON_READ => {
                Source.empty
            }
            case FINITE_BYTESTRING_READ => {
                JSONRestEndpointMethods.TEXT_DOCUMENT_URL_TO_STRING_SOURCE(endpointElement._address)
            }
            case FINITE_KAFKA_READ => {
                val consumer = KafkaUtil.createConsumer()
                Source.empty
               // KafkaUtil.debug(consumer, endpointElement._options("topic"), endpointElement._kafkaPartitionKey, 800000, 30000)
            }
            case FINITE_CASSANDRA_READ => {
                Source.empty
            }
        }
    }
*/
}
