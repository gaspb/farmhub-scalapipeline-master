package org.highjack.scalapipeline.mock

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source, Tcp}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.util.ByteString
import org.highjack.scalapipeline.akka._
import org.highjack.scalapipeline.pipeline._
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, EndpointTypeEnum}
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputTypeEnum}
import org.highjack.scalapipeline.pipeline.transformations.{TransformationElement, TransformationTypeEnum}
import org.highjack.scalapipeline.pipeline.trigger.{TriggerElement, TriggerTypeEnum}
import org.highjack.scalapipeline.executor.PipelineTaskExecutor
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.FiniteDuration._
import scala.concurrent.{ExecutionContext, TimeoutException}
import scala.util.control.NonFatal

/**
  * end-to-end test, called in main (for debug)
  */
class PipelineTaskMock {

    import AkkaStreamLocalContext.system
    import AkkaStreamLocalContext.executor

    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    val restService : RestService.type = RestService

    val decider: Supervision.Decider = {
        case _: TimeoutException => Supervision.Restart
        case NonFatal(e) =>
            logger.error(s"Stream failed with ${e.getMessage}, going to resume")
            Supervision.Resume
    }
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider))

    val doNotParseJSON = true

    def mockThread() : Unit = {
        logger.error("--  mockThread  --")
        PipelineTaskExecutor().run("mock_ppl", "mock_usr", mockPplJson(), "moc_topic")
    }

    def mockPipeline(): PipelineModel = {
        /*
        EXAMPLE ENDPOINT ELEMENT
         */
        val endpointElement:EndpointElement = EndpointElement("mock_ep",  EndpointTypeEnum.AKKA_HTTP_BYTESTRING_READ, Option(MockMain.DOC_SHERLOCK_URL), Option(8080), Option("ep0"), Option(Map.empty) )

        val parseToString:TransformationElement = TransformationElement(1,"mock_parseToString", TransformationTypeEnum.BYTESTRING_TO_STRING, Map.empty)
        val countWords:TransformationElement = TransformationElement(2,"mock_countWords",  TransformationTypeEnum.WORD_OCCURENCE_COUNT, Map(("excludeCommonWords", "true")))
        val tail:TransformationElement = TransformationElement(3,"mock_countWords",  TransformationTypeEnum.STREAM_TAIL, Map.empty)
        val outputElement:OutputElement = OutputElement(4,"out1",Option("to_rest"), OutputTypeEnum.TO_DOWNLOADABLE_FILE)
        val trigger : TriggerElement = TriggerElement("mock_deadtrigger",  Option("some"), TriggerTypeEnum.SIMPLE_RUN )
        val branch : PipelineBranch = PipelineBranch(0,Set[PipelineElement](parseToString,  countWords,tail, outputElement), 1, 0)
        val ppl : PipelineModel = PipelineModel(Set[PipelineBranch](branch), "mock_ppl", endpointElement, trigger)
        ppl
    }


    def fakeStringByteSource(): Source[ByteString, _] = {
        val interval : FiniteDuration =   FiniteDuration(2000, TimeUnit.MILLISECONDS)
        val base : String = "somebytestring"
        Source.tick(interval,interval,ByteString(base))
    }


    def echoTCP_WRITE(): Flow[ByteString, ByteString, _] = {
      TCPManager.outputToTCPFlow(TCPManager.gtwHost, TCPManager.gtwPort) //echo
    }
/*    def echoTCP_READ(): Flow[ByteString, ByteString, _] = {
        TCPManager.registerHostFlow(TCPManager.host, TCPManager.port) //echo
    }*/


    def mockPplJson(): PipelineModel = {
        val json = """
  {
    "pipelineId" : "ppl1",
    "endpoint" : {
      "name" : "mock_edp",
      "endpointType" : "MOCK_TCP",
      "address" : "",
      "port" : 0,
      "kafkaInputKey" : "",
      "options" : {}
    },
    "trigger" : {
     "name" : "tg_1",
     "outputEndpointURL" : "tg1",
     "ttype" : "SIMPLE_RUN"
    },
    "branches" : [{
        "elements": [
            {
                  "elementType": "TRANSFORMATION",
                  "name": "log",
                  "ttype": "MOCK_LOG",
                  "opt": {}
             },{
                "elementType": "OUTPUT",
                 "name": "out",
                 "outputEndpointURL" : "",
                 "otype": "MOCK_TCP"
            }
        ],
        "branchId" :  0,
        "parentBranchId" : 0,
        "startIdxAtParent": 0
    }]
  }
  """
    PipelineModelJSONParser.parse(json)
    }
}

object PipelineTaskMock {
    val DOC_SHAKESPEAR_URL:String = "https://ocw.mit.edu/ans7870/6/6.006/s08/lecturenotes/files/t8.shakespeare.txt"
    val DOC_SHERLOCK_URL:String = "http://norvig.com/big.txt"
    val JSON_CHUCK_NORRIS_STREAM:String = "https://api.chucknorris.io/jokes/random"
}
