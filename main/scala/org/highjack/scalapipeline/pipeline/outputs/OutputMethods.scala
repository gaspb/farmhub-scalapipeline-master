package org.highjack.scalapipeline.pipeline.outputs

import akka.Done
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.highjack.scalapipeline.akka.AkkaRestServer
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

/**
  * Base Output generic methods => TODO move in filesystem (complete with other integrations)
  */
object OutputMethods {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def WRITE_SOURCE_MAP_TO_KAFKA[K,V](kafkaProducer:KafkaProducer[String, String], topic:String ,key:String, source: Source[Map[K,V],_], take:Int) (implicit materializer: Materializer): Future[Map[K,V]] = {
        logger.info("<<<<<<< ---------     in writeSourceToKafka  ------------")
        val tmpMap = Map.empty[K,V]
        source.runFold(tmpMap) {
                (previous, map) => {
                val tmpMap = map.take(20)
                val data = new ProducerRecord[String, String](topic, key, tmpMap.toString)
                logger.info("<<<<<<< ---------     Writing to kafka  ------------" + data.value().length)
                logger.debug(data.toString)
                kafkaProducer.send(data)
                tmpMap
             }
        }
    }
    /**
      * InputStream is available at encrypted url retrieved from POST(scala-ms-ppl/getOutputURL) {pipeline_id=XXX, output_name=XXX}
      */
    def EXPOSE_SOURCE_STREAM[K](pipelineId:String, outputElementName:String, outputEndpointURL:String, source:Source[K,_], take:Int): Future[_] = {
        logger.info("<<<<<<< ---------     in exposeSourceStream  ------------")
        AkkaRestServer.exposeOutputAkkaStream(pipelineId, outputElementName, outputEndpointURL,  source.map(k=>ByteString(k.toString.getBytes)))
        Future.successful(Done)
    }

    def WEBSOCKET(source:Source[_,_]): Future[_] = {
        source.map(k=>ByteString(k.toString.getBytes))
        Future.successful(Done)
    }

}
