package org.highjack.scalapipeline.scalaThreads

import java.util.{Collections, Properties}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import org.apache.kafka.clients.producer.KafkaProducer
import org.highjack.scalapipeline.pipeline._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Future, TimeoutException}
import scala.util.control.NonFatal
/**
  *
  * A ScalaThread is an executed pipeline.
  *
  * The data enters with the ENDPOINT element, either listening to incoming data at a specified endpoint,
  * subscribing to a kafka channel or reading data from an endpoint at an interval or querying a database
  * Ingested data is then passed to further elements.
  *
  * TRANSFORMATION elements : incoming data is gathered in-memory until a size threshold is reached, or split if it exceeds a size limit
  * then processed with spark and passed on
  *
  * TRANSACTION elements persists data to a database
  *
  * MODEL TRAINING elements are pipeline abstractions relying on the three above elements and a specific configuration
  * to produce predictive analysis, human-readable and easy to display as graphs
  *
  *
  *
  */
class ScalaThread(pipelineId:String, userId:String, scalaPipeline: PipelineModel) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def _userId: String = userId
    def _pipelineId: String = pipelineId
    def _cassandraHost : String = "" //TODO

    var _kafkaBrokers : String = "192.168.99.100:9092"
    def _scalaPipeline:PipelineModel = scalaPipeline
    def HOST_TOPIC:String = "topic-jhipster"
   // var manager:PipelineCassandraManager = _
    var kafkaProducer : KafkaProducer[String, String] = _
    def _kafkaProducer : KafkaProducer[String, String] = kafkaProducer
    val STRING_DESERIALIZER =  "org.apache.kafka.common.serialization.StringDeserializer"
    val STRING_SERIALIZER =  "org.apache.kafka.common.serialization.StringSerializer"
    val self:ScalaThread = this
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

    def start : Future[Any] = {
        logger.info("Starting scala thread")
      /*  manager = initCassandraContext(_userId, _pipelineId)*/
      //kafkaProducer = initKafkaProducer()
/*
        //read and run pipeline
        _scalaPipeline._branches foreach ((branch:PipelineBranch)=> {
            val elementsSet = branch._elements//TODO
            if (branch.wrapKafka) {
               /* val endpointElement = elementsSet.head.asInstanceOf[EndpointElement]
                KafkaElementMethods.KAFKA_COMMITABLE_SUBSCRIBE_IN(endpointElement.kafkaInputTopic, endpointElement.kafkaInputKey)
                  .mapAsync(1)(msg =>
                    businessLogic(msg.record).map(_ => msg.committableOffset)
                  )
               KafkaElementMethods.KAFKA_COMMITABLE_SUBSCRIBE_OUT
               */
            }
            //TODO allow on a boolean to run business logic like transformations directly inside a .map to avoid calling it too much
            //or 2 elements, TO_SOURCE and FROM_SOURCE
            val elemVec:Vector[PipelineElement] = elementsSet.toVector
            var retVal:AnyRef = consumeElementsSet(elemVec, startPipelineElement(elemVec(0), retVal), 0)
           /*
             elementsSet foreach  ((elem:PipelineElement) => {

                val ret  = startPipelineElement(elem, retVal) //for now branches are linear, a node always recieve data from the previous node
                retVal = ret
            })*/



s


        })*/
        //V3 :
        // 1) build the logic Flow
        Future.successful(
            LogicBuilder(_scalaPipeline)
                .buildLogicFlow()
                .buildEndpoint()
                .setUpTrigger()
        )

    }


  /*  def consumeElementsSet(elems:Vector[PipelineElement], retVal:AnyRef, idx:Int): (AnyRef, Int) ={
        if (elems.length>idx ) {
            var i = idx + 1
            val next = elems(i)
            if(next.recursive) {
                retVal match {
                    case s:Source[AnyRef, _] => {
                        var lastIdx = 0
                        s.map(some => {
                            val res:(AnyRef, Int) = consumeElementsSet(elems, startPipelineElement(next, some), i)
                            lastIdx = res._2
                        })
                        (s, lastIdx)

                    }
                    case s:Flow[AnyRef, AnyRef, _] => {
                        var lastIdx = 0
                        s.map(some => {
                            val res:(AnyRef, Int) = consumeElementsSet(elems, startPipelineElement(next, some), i)
                            lastIdx = res._2
                        })
                        (s, lastIdx)
                    }
                }
            } else if(next.stopRecursion) {
                (retVal, i)
            } else {
                consumeElementsSet(elems, startPipelineElement(next, retVal), i)
            }
        } else (retVal, idx+1)
    }*/

    def stop() : Unit = {
        kafkaProducer.close()
       //manager.close()
       //release akka in- memory
       //maybe delete kafka and cassandra data
    }


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

   /* def startPipelineElement(element: PipelineElement, retVal: AnyRef) : AnyRef = {
        logger.error("Starting element "+element.name+ " , retval="+retVal)
        var ret = retVal

        element.elemType match {
            case PipelineElementTypeEnum.ENDPOINT => {//AKKA HTTP / KAFKA TO AKKA SOURCE
                ret = EndpointExecutor(element.asInstanceOf).run()

            }
            case PipelineElementTypeEnum.TRANSFORMATION => {//AKKA FLOW + SPARK
                ret = TransformationExecutor(element.asInstanceOf).run(retVal.asInstanceOf)

            }
            case PipelineElementTypeEnum.TRANSACTION => {//SOURCE -> AKKA SINK or FLOW -> CASSANDRA

            }
            case PipelineElementTypeEnum.MODEL_TRAINING => {//AKKA ask KAFKA or ask CASSANDRA feed SPARK+MLLIB feed KAFKA

            }
            case PipelineElementTypeEnum.OUTPUT => { //AKKA ask KAFKA (ask SPARK?) ask CASSANDRA feed AKKA
                logger.error(" ---------    Running output  ------------")
                // either SINK -> STREAM/DATA or SINK.ignore and runnable by proxy call
                ret = OutputExecutor(element.asInstanceOf).run(retVal.asInstanceOf[Source[Map[String, Long],_]], self)
            }
            case PipelineElementTypeEnum.TRIGGER => { //AKKA SINK THAT RUN THE PPL OR SINK.IGNORE TO RUNNABLE AND EXPOSE ENDPOINT

            }
            //case trigger : run the previous part of the pipeline :
            // can be :
            // -source via flow copyDataToAnotherSource to sink.ignore .run
            // -
        }
        ret

    }*/

}

object ScalaThread {

}
