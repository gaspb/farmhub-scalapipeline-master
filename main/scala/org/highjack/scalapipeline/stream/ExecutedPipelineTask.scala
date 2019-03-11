package org.highjack.scalapipeline.stream

import java.util.{Collections, Properties}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import org.apache.kafka.clients.producer.KafkaProducer
import org.highjack.scalapipeline.kafka.KafkaConstants
import org.highjack.scalapipeline.pipeline._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Future, TimeoutException}
import scala.util.control.NonFatal


/**
  *
  * The data enters with the ENDPOINT element, either listening to incoming data at a specified endpoint,
  * subscribing to a kafka channel or reading data from an endpoint at an interval or querying a database
  * Ingested data is then passed to further elements.
  *
  * TRANSFORMATION elements : incoming data is gathered in-memory until a size threshold is reached, or split if it exceeds a size limit
  * then processed with spark, scala or python and passed on
  *
  * TRANSACTION elements persists data to a database
  *
  * MODEL TRAINING elements are pipeline abstractions relying on the three above elements and a specific configuration
  * to produce predictive analysis, human-readable and easy to display as graphs
  *
  *
  *
  */
class ExecutedPipelineTask(pipelineId:String, userId:String, scalaPipeline: PipelineModel) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def _userId: String = userId
    def _pipelineId: String = pipelineId
    //def _cassandraHost : String = ""

    var _kafkaBrokers : String = KafkaConstants.KAFKA_BROKER + ":9092"
    def _scalaPipeline:PipelineModel = scalaPipeline
    def HOST_TOPIC:String = "topic-jhipster" //TODO
   // var manager:PipelineCassandraManager = _
    var kafkaProducer : KafkaProducer[String, String] = _
    def _kafkaProducer : KafkaProducer[String, String] = kafkaProducer
    val STRING_DESERIALIZER =  KafkaConstants.STRING_DESERIALIZER
    val STRING_SERIALIZER =  KafkaConstants.STRING_SERIALIZER
    val self:ExecutedPipelineTask = this
    implicit val system = ActorSystem()

    var kafkaMessageWrapping:Boolean = false

    val decider: Supervision.Decider = {
        case _: TimeoutException => Supervision.Restart
        case NonFatal(e) =>
            logger.error(s"Stream failed with ${e.getMessage}, going to resume")
            Supervision.Resume
    }
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider))


    /**
      * Launch pipeline
      */
    def start : Future[Any] = {
        logger.info("Starting scala thread")
      /*  manager = initCassandraContext(_userId, _pipelineId)*/
        Future.successful(
            LogicBuilder(_scalaPipeline)
                .buildLogicFlow()
                .buildEndpoint()
                .setUpTrigger()
        )
    }


    def stop() : Unit = {
        kafkaProducer.close()
       //manager.close()
       //release akka in-memory
    }

//TODO make a dev0 profile without cassandra
    /*def initCassandraContext(userId:String, pipelineId:String): PipelineCassandraManager = {
        new PipelineCassandraManager(userId, pipelineId, "0")//init and create table 0
    }*/

    def initKafkaProducer(): KafkaProducer[String, String] = {
        val props = new Properties()
        props.put("bootstrap.servers", _kafkaBrokers) //or put(metadata.broker.list)
        props.put("client.id", "ScalaPipelineMS")
        props.put("key.serializer", STRING_SERIALIZER)
        props.put("value.serializer", STRING_SERIALIZER)
        new KafkaProducer[String, String](props)
    }
}
