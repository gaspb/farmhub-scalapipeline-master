package org.highjack.scalapipeline.akka

import AkkaStreamLocalContext._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{Future, TimeoutException}
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal


class AkkaStreamStatsMethods[T]()(implicit m: Manifest[T]) {

    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    val decider: Supervision.Decider = {
        case _: TimeoutException => Supervision.Restart
        case NonFatal(e) =>
            logger.error(s"Stream failed with ${e.getMessage}, going to resume")
            Supervision.Resume
    }
    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider))

    /**
      * Parse the byte source to a given Class source
      */
    def parseByteSourceToClassSource(restartSource : Source[ByteString, _],
                                     idleDuration: FiniteDuration,
                                     doNotParseJSON : Boolean,
                                     jsonDelimiter : Option[ByteString])(implicit tRds : Reads[T]): Source[T, _] = {
        restartSource
            .idleTimeout(idleDuration)
            .via(Framing.delimiter(
            ByteString("\n"),
            maximumFrameLength = 4096,
            allowTruncation = false).async)
              .grouped(100)
              .map(s =>
                  s.flatMap(b=>b.toArray[Byte]
                  ).toArray[Byte]).async
            .map(str =>
                if(!doNotParseJSON) Json.parse(str).as[T] else ByteString(str).utf8String.asInstanceOf[T]
            )
    }


    def runAndPrintGlobalStats(restartSource : Source[Map[String, Long], _], topCount : Int): Future[String] = {
        restartSource.runFold[String] ("") { (prevStats:String, wc:Map[String, Long]) => {
            wc.take(topCount).map { case (k, v) => s"$k:$v\n" }.mkString("  ")
            }
        }
    }

    def runAndPrintIncrementalStats(restartSource : Source[Map[String, Long], _], topCount : Int): Future[List[String]] = {
        val record : List[String] = List.empty
        restartSource.runFold[List[String]] (record) {
            (prevStats:List[String], wc:Map[String, Long]) => {
                val curr = wc.take(topCount).map { case (k, v) => s"$k:$v\n" }.mkString("  ")
                record + curr
                record
            }
        }
    }

}
