package org.highjack.scalapipeline.mock

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import org.highjack.scalapipeline.akka._
import org.highjack.scalapipeline.pipeline._
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, EndpointTypeEnum}
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputTypeEnum}
import org.highjack.scalapipeline.pipeline.transformations.{TransformationElement, TransformationTypeEnum}
import org.highjack.scalapipeline.pipeline.trigger.{TriggerElement, TriggerTypeEnum}
import org.highjack.scalapipeline.scalaThreadExecutor.ScalaThreadExecutor
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.{ExecutionContext, TimeoutException}
import scala.util.control.NonFatal

class MockMain2 {


    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    val restService : RestService.type = RestService
    implicit val system = ActorSystem()
    implicit val executionContext : ExecutionContext =system.dispatcher

    val decider: Supervision.Decider = {
        case _: TimeoutException => Supervision.Restart
        case NonFatal(e) =>
            logger.error(s"Stream failed with ${e.getMessage}, going to resume")
            Supervision.Resume
    }
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider))
    //mock 1 : count words from delimited string

    val doNotParseJSON = true

    def mockThread() : Unit = {
        logger.error("--  mockThread  --")
        ScalaThreadExecutor().run("mock_ppl", "mock_usr", mockPplJson(), "moc_topic")
    }

    def mockPipeline(): PipelineModel = {

        /*
        EXAMPLE ENDPOINT ELEMENT
         */
        // val endpointElement:EndpointElement = EndpointElement("mock_ep", 1, false, 0, EndpointTypeEnum.REST_TEXTFILE_STREAM, MockMain.DOC_SHAKESPEAR_URL, "ep0", Map.empty )
        val endpointElement:EndpointElement = EndpointElement("mock_ep",  EndpointTypeEnum.AKKA_HTTP_BYTESTRING_READ, MockMain.DOC_SHERLOCK_URL, 8080, "ep0", Map.empty )

      /*  val parseJsonTransfo:TransformationElement = TransformationElement("mock_tf_str", 2, false, 1, true, TransformationTypeEnum.PARSE_JSON_OBJECT, Map.empty)

        val getJsonPropertyTreansfo:TransformationElement = TransformationElement("mock_tf_str", 2, false, 1, true, TransformationTypeEnum.PARSE_JSON_OBJECT, Map.empty)
*/
        val parseToString:TransformationElement = TransformationElement("mock_parseToString", TransformationTypeEnum.BYTESTRING_TO_STRING, Map.empty)
        val countWords:TransformationElement = TransformationElement("mock_countWords",  TransformationTypeEnum.WORD_OCCURENCE_COUNT, Map(("excludeCommonWords", "true")))
        val tail:TransformationElement = TransformationElement("mock_countWords",  TransformationTypeEnum.STREAM_TAIL, Map.empty)
        val outputElement:OutputElement = OutputElement("out1","to_rest", OutputTypeEnum.TO_DOWNLOADABLE_FILE)

        val trigger : TriggerElement = TriggerElement("mock_deadtrigger",  "some", TriggerTypeEnum.SIMPLE_RUN )


        val branch : PipelineBranch = PipelineBranch(Set[PipelineElement](parseToString,  countWords,tail, outputElement), 1, 0,0)
        val ppl : PipelineModel = PipelineModel(Set[PipelineBranch](branch), "mock_ppl", endpointElement, trigger)
        ppl
    }

    def mockPplJson(): PipelineModel = {
        val json = """
  {
    "pipelineId" : "ppl1",
    "endpoint" : {
      "name" : "mock_edp",
      "endpointType" : "AKKA_HTTP_BYTESTRING_READ",
      "address" : "http://norvig.com/big.txt",
      "port" : 8080,
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
                "name": "parseStr",
                "ttype": "BYTESTRING_TO_STRING",
                "opt": {}
            },{
                 "elementType": "TRANSFORMATION",
                 "name": "countWords",
                 "ttype": "WORD_OCCURENCE_COUNT",
                 "opt": {"excludeCommonWords" : "true",
                            "top"            : "20"}
            },{
                  "elementType": "TRANSFORMATION",
                  "name": "tail",
                  "ttype": "STREAM_TAIL",
                  "opt": {}
             },{
                "elementType": "OUTPUT",
                 "name": "out",
                 "outputEndpointURL" : "myFile",
                 "otype": "TO_DOWNLOADABLE_FILE"
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
