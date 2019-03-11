package org.highjack.scalapipeline.pipeline.endpoints

import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.model.HttpHeader
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.{AkkaRestServer, AkkaStreamStatsMethods, RestService}
import org.highjack.scalapipeline.utils.PerfUtil
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Seq
import scala.concurrent.duration.FiniteDuration

/**
  * Temporary generic "Mock" methods, to be replaced with user-written code or saved in filesystem
  */
object JSONRestEndpointMethods {

    val akkaStreamHandlerDOC  = new AkkaStreamStatsMethods[String]()
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def TEXT_DOCUMENT_URL_TO_RUNNABLE(url:String, port:Int, flow:Flow[ByteString,_,_]) : RunnableGraph[Any] = { //TODO port
        val source = RestService.akkaByteDocumentStream(url, Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(40, "seconds"))
        akkaStreamHandlerDOC.parseByteSourceToClassSource(source,  FiniteDuration.apply(30, "seconds"), true, Option(ByteString("\n"))).async
        .map(ByteString.apply).async
            .via(flow).async.map(s=>{
            logger.info("got "+s) //DEBUG
            s
        }).to(Sink.onComplete( s => {
            logger.info("debug--ended")//DEBUG
            PerfUtil.stopAndLog()
        }))
    }

    def JSON_STREAM_API_URL_TO_OBJECT_SOURCE[A](url:String)(implicit aRds : Reads[A], m: Manifest[A]) : Source[A, _] = {
        val akkaStreamHandlerG  = new AkkaStreamStatsMethods[A]()

        val source = RestService.akkaByteDocumentStream(url, Seq.empty[HttpHeader], FiniteDuration.apply(3, "seconds"), FiniteDuration.apply(40, "seconds"))
        akkaStreamHandlerG.parseByteSourceToClassSource(source,  FiniteDuration.apply(30, "seconds"), false, Option(ByteString("\n")))
    }

}
