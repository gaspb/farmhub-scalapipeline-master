package org.highjack.scalapipeline.akka

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json, Reads}

import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal


class AkkaStreamHandler[T]()(implicit m: Manifest[T]) {

    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    implicit val system = ActorSystem()
    implicit val exec: ExecutionContext =system.dispatcher
    // Akka-Streams materializer instance
    val decider: Supervision.Decider = {
        case _: TimeoutException => Supervision.Restart
        case NonFatal(e) =>
            logger.error(s"Stream failed with ${e.getMessage}, going to resume")
            Supervision.Resume
    }
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider))

    /**
      * 1) Parse the byte source to a given Class source
      * //TODO option : use json4s API to find only target JSON property before further processing
      */
    def parseByteSourceToClassSource(restartSource : Source[ByteString, _], idleDuration: FiniteDuration,
                                     doNotParseJSON : Boolean, jsonDelimiter : Option[ByteString])(implicit tRds : Reads[T]): Source[T, _] = {

        restartSource
            // throws TimeoutException when no elements are available from the Source
            .idleTimeout(idleDuration)
            // 'scan' has a state inside the stage. Below 'acc' variable is exactly the state, which
            // we can reset by returning a new or empty accumulator
          /*  .scan("")((acc, curr) =>
            // if JSON document delimiter "\r\n" is occured, then we start new document accumulation
            if (acc.contains(jsonDelimiter getOrElse ByteString("\n"))) {/*logger.error("---------DIVIDING--------"+curr.utf8String);*/curr.utf8String} // TODO
            // otherwise, we accumulate fragements of incominng document
            else acc + curr.utf8String
        )*/
            .via(Framing.delimiter(
            ByteString("\n"),
            maximumFrameLength = 4096,
            allowTruncation = false).async)
              .grouped(100)
              .map(s => s.flatMap(b=>b.toArray[Byte]).toArray[Byte]).async
            .map(str => if(!doNotParseJSON) Json.parse(str).as[T] else ByteString(str).utf8String.asInstanceOf[T])
    }



    def runAndPrintGlobalStats(restartSource : Source[Map[String, Long], _], topCount : Int): Future[String] = {
        restartSource.runFold[String] ("") { (prevStats:String, wc:Map[String, Long]) => {
            wc.take(topCount).map { case (k, v) => s"$k:$v\n" }.mkString("  ")
            }
        }
    }
    def runAndPrintIncrementalStats(restartSource : Source[Map[String, Long], _], topCount : Int): Future[List[String]] = {
        val record : List[String] = List.empty
        restartSource.runFold[List[String]] (record) { (prevStats:List[String], wc:Map[String, Long]) => {
            val curr = wc.take(topCount).map { case (k, v) => s"$k:$v\n" }.mkString("  ")
            record + curr
            record
        }
        }


    }



}
