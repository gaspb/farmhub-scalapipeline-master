package org.highjack.scalapipeline.mock

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.apache.kafka.clients.producer.KafkaProducer
import org.highjack.scalapipeline.akka._
import org.highjack.scalapipeline.pipeline._
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, EndpointTypeEnum}
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputMethods}
import org.highjack.scalapipeline.pipeline.transformations.{StringSourceToStatsMethods, TransformationElement}
import org.highjack.scalapipeline.executor.PipelineTaskExecutor
import org.highjack.scalapipeline.stream.ExecutedPipelineTask
import org.highjack.scalapipeline.scalaTransformation.ScalaTransformation
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Reads}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try
import scala.util.control.NonFatal

@deprecated
class MockMain {

/*

    val logger : Logger = LoggerFactory.getLogger(this.getClass)


    val akkaRestServer : AkkaRestServer.type = AkkaRestServer
    val akkaStreamHandlerMOCKVM  = new AkkaStreamHandler[MockVM]()
    val akkaStreamHandlerDOC  = new AkkaStreamHandler[String]()
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
    case class MockVM(id: String, url: String, value: String )

    implicit val mockVmRds : Reads[MockVM] = (
        (__ \ "id").read[String] ~
            (__ \ "url").read[String] ~
            (__ \ "value").read[String]
        )(MockVM)

    val doNotParseJSON = true
     def startChuckNorris(): Unit = {
        val restartSource : Source[ByteString, NotUsed]  = restService.akkaByteStreamRestartSource("https://api.chucknorris.io/jokes/random", Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(20, "seconds"))
        val classSource = akkaStreamHandlerMOCKVM.parseByteSourceToClassSource(restartSource,  FiniteDuration.apply(200, "seconds"), !doNotParseJSON, Option("}"))(mockVmRds)
        val propertySource = classSource.map(_.value)
         val statsSource = mockWordCountTranformationRun(propertySource,  true)
         akkaStreamHandlerMOCKVM.runAndPrintIncrementalStats(statsSource, 15)
    }

    def startLongTextStream2(url:String): Unit = {
        //ENDPOINT
        val stringSource : Source[String, _]  = mockStringSourceEndpointRun(url, doNotParseJSON, Option("\n"))
       //TRANSFORMATION
        val statsSource = mockWordCountTranformationRun(stringSource, true)
       //OUTPUT
        mockMapPrintOutputRun(statsSource,20) onComplete { (f:Try[String])=>
            logger.info("<<<<<<<<<<<<<<<<<<  Mock complete  >>>>>>>>>>>>>>>>>")
            logger.info("<<<<<<<<<<<<<<<<<<<<------------>>>>>>>>>>>>>>>>>>>>");
        }
    }

    //val mockEndpointElement = _
    def mockStringSourceEndpointRun(url:String, doNotParseJSON:Boolean, optionalDelimiter:Option[String]):Source[String, _] = {
        val source = restService.akkaByteDocumentStream(url, Seq.empty[HttpHeader], FiniteDuration.apply(20, "seconds"), FiniteDuration.apply(40, "seconds"))
        akkaStreamHandlerDOC.parseByteSourceToClassSource(source,  FiniteDuration.apply(20, "seconds"), doNotParseJSON, optionalDelimiter)
    }

    /**
      * Transforms a Source[String, _] to a statistic Source[Map[String, Int], _] counting word occurences in incoming stream
      */
   // val mockTranformationElement = _
    def mockWordCountTranformationRun(source:Source[String, _], excludeCommonWords:Boolean): Source[Map[String, Long], _]  = {
        StringSourceToStatsMethods.STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE(source, excludeCommonWords)
    }

  //  val mockOutputElement = _
    def mockMapPrintOutputRun(source:Source[Map[String, Long], _], numberToDisplay:Int): Future[String]  = {
        akkaStreamHandlerDOC.runAndPrintGlobalStats(source, numberToDisplay) andThen {case ev:(Try[String]) =>
            val statsStr:String = ev.get
            println("isSuccess:"+ev.isSuccess)
            println("")
            println("-----------------GLOBAL STATS--------------- , statsStr.length="+statsStr.length)
            println("\r" + statsStr)
            statsStr
        }
    }

    def mockThread() = {
        logger.error("--  mockThread  --")
        ScalaThreadExecutor().run("mock_ppl", "mock_usr", mockPipeline(), "moc_topic")
    }

    def mockPipeline(): PipelineModel = {

        /*
        EXAMPLE ENDPOINT ELEMENT
         */
        // val endpointElement:EndpointElement = EndpointElement("mock_ep", 1, false, 0, EndpointTypeEnum.REST_TEXTFILE_STREAM, MockMain.DOC_SHAKESPEAR_URL, "ep0", Map.empty )
        val endpointElement:EndpointElement = EndpointElement("mock_ep", 1, false, 0, EndpointTypeEnum.REST_JSON_STREAM, MockMain.JSON_CHUCK_NORRIS_STREAM, "ep0", Map.empty )

        val transformationElement0:TransformationElement[JsValue, String] = TransformationElement(
            "mock_tf_str", 2, false, 1, new ScalaTransformation[JsValue, String] {
                override def run(rawData: Source[JsValue, _], args: Option[Any]): Source[String, _] = {
                    rawData.map(m=> {
                        (m \ "value").get.toString
                    })
                }
            })

        val transformationElement:TransformationElement[String, Map[String, Long]] = TransformationElement(
            "mock_tf_str", 3, false, 2, new ScalaTransformation[String, Map[String, Long]] {
                override def run(rawData: Source[String, _], args: Option[Any]): Source[Map[String, Long], _] = {
                    StringSourceToStatsMethods.STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE(rawData, true)
                }
        })
        val outputElement:OutputElement[Map[String, Long]] = new OutputElement[Map[String, Long]]("out1",4,3,"", (source : Source[Map[String, Long], _], thread:ScalaThread)=>{
            logger.info(" ---------   RUN OUT1 ------------{}",thread._pipelineId)
            val takeTop20:Int = 20
      //      OutputMethods.WRITE_SOURCE_MAP_TO_KAFKA(thread._kafkaProducer, thread.HOST_TOPIC, "mock_ppl/out1", source, takeTop20)
            //akkaStreamHandlerMOCKVM.runAndPrintIncrementalStats(source, 15)
            OutputMethods.EXPOSE_SOURCE_STREAM( "mock_ppl","out1", source, 20)
        })

        val branch : PipelineBranch = PipelineBranch(Set[PipelineElement](endpointElement, transformationElement0,  transformationElement, outputElement), 1, 0,0)
        val ppl : PipelineModel = PipelineModel(Set[PipelineBranch](branch), "mock_ppl")
        ppl
    }
*/


/*
    val url = new java.net.URL("https://api.twitter.com/1/trends/1.json")
    val content = Source.fromInputStream(url.openStream).getLines.mkString("\n")
    val trends = Json.parse(content) match {
        case JsArray(Seq(t)) => Some((t \ "trends").as[Seq[Trend]])
        case _ => None
    }
*/
}
object MockMain {
    val DOC_SHAKESPEAR_URL:String = "https://ocw.mit.edu/ans7870/6/6.006/s08/lecturenotes/files/t8.shakespeare.txt"
    val DOC_SHERLOCK_URL:String = "http://norvig.com/big.txt"
    val JSON_CHUCK_NORRIS_STREAM:String = "https://api.chucknorris.io/jokes/random"
}
