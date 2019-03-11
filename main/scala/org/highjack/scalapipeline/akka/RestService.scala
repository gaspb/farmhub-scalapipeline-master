package org.highjack.scalapipeline.akka

import akka.NotUsed
import AkkaStreamLocalContext.system
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Flow, RestartSource, Source}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

object RestService {

    val logger : Logger = LoggerFactory.getLogger(RestService.getClass)
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

    def akkaByteStreamRestartSource(url:String, headers:Seq[HttpHeader],
                                    minBackoff:FiniteDuration,
                                    maxBackoff : FiniteDuration)(implicit ec: ExecutionContext): Source[ByteString, NotUsed] = {
        val restartSource = RestartSource.withBackoff(
            minBackoff,
            maxBackoff,
            0.2 // adds 20% "noise" to vary the intervals slightly
        ) { () =>
            Source.fromFutureSource {
                val response = Http(system).singleRequest(HttpRequest(uri = url, headers = headers))
                response.failed.foreach(t => logger.error(s"Request has been failed with $t"))
                response.map(resp => {
                    resp.status match {
                        // if status is OK, then getting instance of Source[ByteString, NotUsed]
                        case StatusCodes.OK => resp.entity.withoutSizeLimit().dataBytes
                        // if not OK, then logging error and returning failed Source to restart stream
                        case code =>
                            val text = resp.entity.dataBytes.map(_.utf8String)
                            val error = s"Unexpected status code: $code, $text"
                            logger.error(error)
                            Source.failed(new RuntimeException(error))
                    }
                })
            }
        }
        restartSource
    }


    //TEST
    /*def akkaByteStream(url:String, port:String, headers:Seq[HttpHeader])(implicit ec: ExecutionContext): Source[ByteString, Any] = {
        logger.info("----Requesting2")
        val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
            Http().outgoingConnection(url)
        Source.empty
    }*/


    def akkaByteDocumentStream(url:String, headers:Seq[HttpHeader], minBackoff:FiniteDuration, maxBackoff : FiniteDuration)(implicit ec: ExecutionContext): Source[ByteString, _] = {
        Source.single("").via(
            Flow[String].map((f:String)=>{
                logger.info("----Requesting1 url "+url)
                val response = Http(system).singleRequest(HttpRequest(uri = url, headers = headers))

                response.failed.foreach(t => logger.error(s"Request has been failed with $t"))

                response.map[Source[ByteString, Any]](resp => {
                    resp.status match {
                        case StatusCodes.OK => {resp.entity.withoutSizeLimit().dataBytes}
                        case code =>
                            val text = resp.entity.dataBytes.map(_.utf8String)
                            val error = s"Unexpected status code: $code, $text"
                            logger.error(error)
                            Source.failed(new RuntimeException(error))
                    }
                })
            }
            )).flatMapConcat(f => Source.fromFutureSource(f)).map(b=>{
            //DEBUG
            logger.info("RestService - Bytestring of size "+b.length)
            b
        })
    }

}
