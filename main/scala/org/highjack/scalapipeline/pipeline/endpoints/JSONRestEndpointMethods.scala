package org.highjack.scalapipeline.pipeline.endpoints



import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.model.HttpHeader
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.{AkkaRestServer, AkkaStreamHandler, RestService}
import org.highjack.scalapipeline.utils.PerfUtil
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Seq
import scala.concurrent.duration.FiniteDuration

/**
  * Created by High Jack on 20/10/2018.
  */
object JSONRestEndpointMethods {
    val akkaRestServer : AkkaRestServer.type = AkkaRestServer

    val akkaStreamHandlerDOC  = new AkkaStreamHandler[String]()
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def TEXT_DOCUMENT_URL_TO_RUNNABLE(url:String, port:Int, flow:Flow[ByteString,_,_]) : RunnableGraph[Any] = { //TODO port
        val source = RestService.akkaByteDocumentStream(url, Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(40, "seconds"))
        akkaStreamHandlerDOC.parseByteSourceToClassSource(source,  FiniteDuration.apply(30, "seconds"), true, Option(ByteString("\n"))).async
              /*.map(t=>{logger.info("-------------------CHUNK--------------------"+t);t})*/
          .map(ByteString.apply).async
          .via(flow).async
          .to(Sink.onComplete( s => PerfUtil.stopAndLog()))



    }
    def JSON_STREAM_API_URL_TO_OBJECT_SOURCE[A](url:String)(implicit aRds : Reads[A], m: Manifest[A]) : Source[A, _] = {
        val akkaStreamHandlerG  = new AkkaStreamHandler[A]()

        val source = RestService.akkaByteDocumentStream(url, Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(40, "seconds"))
        akkaStreamHandlerG.parseByteSourceToClassSource(source,  FiniteDuration.apply(30, "seconds"), false, Option(ByteString("\n")))
    }
   /* def _JSON_STREAM_API_URL_TO_JSON_OBJECT_SOURCE_TEST(url:String) : Source[_, _] = {
        val source = RestService.akkaByteStreamRestartSource(url, Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(30, "seconds"))
        akkaStreamHandlerDOC.parseByteSourceToJSONSource(source,  FiniteDuration.apply(30, "seconds"), Option("}"))

    }*/

//CASSANDRA
    /*
    Source.fromPublisher(db.stream(things.result.transactionally.withStatementParameters(fetchSize = 1000))) Where things is a a TableQuery
     */

}
