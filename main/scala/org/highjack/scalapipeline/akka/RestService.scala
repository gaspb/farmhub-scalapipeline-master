package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Flow, RestartSource, Source, StreamConverters}
import akka.util.ByteString

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration
/**
  * Created by High Jack on 17/10/2018.
  */
object RestService {
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
    implicit val system = ActorSystem()

   /* def simpleGetString(url:String): String = {
        val response: HttpResponse[String] = Http(url).asString
        response.body
    }
    def getStringWithParams(url:String, params : Seq[(String, String)] ): String = {
        val response: HttpResponse[String] = Http(url).params(params).asString
        response.body
    }

    def streamFromRestJsonPoll(url:String, _interval:Int, _timeout:Int, _maxSize:Long): Unit = {
        Http(url).execute(is => {
            scala.io.Source.fromInputStream(is).getLines().foreach(println)
        })
    }

    def restStreamAsStringIterator(url:String ) : Iterator[String] = {
        val response : HttpResponse[Iterator[String]] = Http(url).execute(is => {
            scala.io.Source.fromInputStream(is).getLines()
        })
        response.body
    }
    def restStreamAsString(url:String ) : String = {
        val response : HttpResponse[String] = Http(url).execute(is => {
            scala.io.Source.fromInputStream(is).mkString
        })
        response.body
    }

    //scala.io.Source.fromInputStream(is).mkString
    def genericRestStream (url:String): Source[ByteString, _] = {
        val response: HttpResponse[Source[ByteString, _]] = Http(url).execute(parser = {inputStream =>
            val dataContent: Source[ByteString, _] = StreamConverters.fromInputStream(() => inputStream)
            dataContent
        })
        response.body
    }
    */
    def akkaByteStreamRestartSource(url:String, headers:Seq[HttpHeader], minBackoff:FiniteDuration, maxBackoff : FiniteDuration)(implicit ec: ExecutionContext): Source[ByteString, NotUsed] = {

        val restartSource = RestartSource.withBackoff(
            minBackoff,
            maxBackoff,
            0.2 // adds 20% "noise" to vary the intervals slightly
        ) { () =>
            Source.fromFutureSource {
                println("----Requesting0")
                val response = Http(system).singleRequest(HttpRequest(uri = url, headers = headers))

                response.failed.foreach(t => System.err.println(s"Request has been failed with $t"))

                response.map(resp => {
                    resp.status match {
                        // if status is OK, then getting instance of Source[ByteString, NotUsed]
                        // to consume data from Twitter server
                        case StatusCodes.OK => resp.entity.withoutSizeLimit().dataBytes
                        // if not OK, then logging error and returning failed Source to try
                        // to restart the stream and issue new HTTP request
                        case code =>
                            val text = resp.entity.dataBytes.map(_.utf8String)
                            val error = s"Unexpected status code: $code, $text"
                            System.err.println(error)
                            Source.failed(new RuntimeException(error))
                    }
                })
            }
        }

        restartSource
    }
    def akkaByteStream(url:String, port:String, headers:Seq[HttpHeader])(implicit ec: ExecutionContext): Source[ByteString, Any] = {

        val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
            Http().outgoingConnection(url)

    Source.empty


    }


    def akkaByteDocumentStream(url:String, headers:Seq[HttpHeader], minBackoff:FiniteDuration, maxBackoff : FiniteDuration)(implicit ec: ExecutionContext): Source[ByteString, _] = {
        Source.fromFutureSource[ByteString, Any] {
            println("----Requesting1")
            val response = Http(system).singleRequest(HttpRequest(uri = url, headers = headers))

            response.failed.foreach(t => System.err.println(s"Request has been failed with $t"))

            response.map[Source[ByteString, Any]](resp => {
                resp.status match {
                    // if status is OK, then getting instance of Source[ByteString, NotUsed]
                    // to consume data from Twitter server
                    case StatusCodes.OK => resp.entity.withoutSizeLimit().dataBytes
                    // if not OK, then logging error and returning failed Source to try
                    // to restart the stream and issue new HTTP request
                    case code =>
                        val text = resp.entity.dataBytes.map(_.utf8String)
                        val error = s"Unexpected status code: $code, $text"
                        System.err.println(error)
                        Source.failed(new RuntimeException(error))
                }
            })
        }
    }

}
