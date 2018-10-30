package org.highjack.scalapipeline.scalaThreads

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import org.apache.commons.codec.binary.Base64
import org.highjack.scalapipeline.pipeline.transformations.{StringSourceToStatsMethods, TransformationElement}
import org.highjack.scalapipeline.pipeline.transformations.TransformationTypeEnum._
import play.api.libs.json.{JsValue, Json}

/**
  * Created by High Jack on 28/10/2018.
  */
case class TransformationsToFlow(el:TransformationElement) {

    def get(): Flow[_,_,NotUsed] ={
        el.ttype match {
            case PARSE_JSON_OBJECT => {
                Flow[ByteString].map(
                    m => Json.parse(m.utf8String)
                )
            }
            case RETRIEVE_PROPERTY_FROM_JSON_AS_STRING => {
                val property : String = el.opt("property").asInstanceOf

                Flow[JsValue].map(
                    m => (m \ property).as[String]
                )
            }
            case RETRIEVE_PROPERTY_FROM_JSON_AS_JSON => {
                val property : String = el.opt("property").asInstanceOf

                Flow[JsValue].map(
                    m => m \ property
                )
            }
            case STRING_TO_BYTESTRING => {
                Flow[String].map(ByteString(_))
            }
            case ANY_TO_BYTESTRING => { //TODO

                Flow[AnyRef].map(m => ByteString(m.toString))
            }
            case BYTESTRING_TO_STRING => {
                Flow[ByteString].map(_.utf8String)
            }
            case WORD_OCCURENCE_COUNT => {
                val excludeCommonWords : Boolean = el.opt.getOrElse("excludeCommonWords", false).toString.toBoolean
                val taketop : Option[Long] = el.opt.get("top").map(f=>f.toString.toLong).orElse(Option.empty)
                //incremental
                StringSourceToStatsMethods.STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE(excludeCommonWords, taketop)
            }
            case STREAM_TAIL => {
                Flow[Any].reduce((t, t2)=>t2)
            }




        }

    }




}
