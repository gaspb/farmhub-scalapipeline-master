package org.highjack.scalapipeline.pipeline.transformations

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.kafka.KafkaUtil
import org.highjack.scalapipeline.pipeline.PipelineElementExecutor
import org.highjack.scalapipeline.pipeline.transformations.TransformationTypeEnum._
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json, Reads, __}

import scala.concurrent.ExecutionContext


case class TransformationExecutor(transformationElement: TransformationElement)(implicit val wrapKafka:Boolean) extends PipelineElementExecutor {

    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    implicit val system = ActorSystem()
    implicit val exec: ExecutionContext =system.dispatcher

    /**
      * Execute a source.via(flow)
      * @param source
      * @param func to apply
      * @return
      */
    def exec[A,B](source:Source[A,_], func: A => B) : Source[B,_] = {
        val flow :Flow[A,B,_] = Flow.fromFunction(a => kafkaSafe(a, func) )
        source.via(flow)
    }


    def run[A<:AnyRef,B](source:Source[A,_]): Source[B,_] ={
        transformationElement.ttype match {
            case GENERIC_CONVERSION => exec(source, asInstanceOf)
        /*    case PARSE_JSON_OBJECT => {
                val jsonDelimiter : Option[String] = transformationElement.opt.get("jsonDelimiter").asInstanceOf
                def utf8String : AnyRef =>String  =
                source match {
                    case s:Source[ByteString,_] =>  byte => ByteString(byte).utf8String
                    case s:Source[String,_] =>  str => str.toString
                    case _ => a => a.toString //TODO error handling
                }
                source
                      .scan(_)((acc:String, curr: ByteString) =>
                    if (unwrap[String](acc).contains(jsonDelimiter getOrElse "\n")) {logger.error("---------DIVIDING--------"+utf8String(curr));utf8String(curr)}
                    else acc + utf8String(curr)
                    )
                    .filterNot(unwrap[String].)
                    .map(curr =>  Json.parse(curr) match {
                        case s:B => wrap(s) //B should be JsValue //TODO error handling
                    })
              }
            case RETRIEVE_PROPERTY_FROM_JSON => {
                val property : String = transformationElement.opt("property").asInstanceOf
                implicit val aRds : Reads[B] = (__ \ property).read[B]

                exec(source, (jsValue:A) => {
                    jsValue.asInstanceOf[JsValue].as
                })
            }
            case WORD_OCCURENCE_COUNT => {

                val excludeCommonWords : Boolean = transformationElement.opt("excludeCommonWords").toString.toBoolean
                //incremental
                StringSourceToStatsMethods.STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE(source.asInstanceOf, excludeCommonWords).asInstanceOf

            }*/
        }
    }

}
/*

idleTimeout(idleDuration)
                .scan("")((acc, curr) =>
                    if (acc.contains(jsonDelimiter getOrElse "\r\n")) {logger.error("---------DIVIDING--------"+curr.utf8String);curr.utf8String}
                    else acc + curr.utf8String
                )
                .filterNot(_.trim.isEmpty)
                .map(curr =>  Json.parse(curr))

 */
