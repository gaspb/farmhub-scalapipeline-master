package org.highjack.scalapipeline.stream

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import org.highjack.scalapipeline.pipeline.transformations.{StringSourceToStatsMethods, TransformationElement}
import org.highjack.scalapipeline.pipeline.transformations.TransformationTypeEnum._
import org.highjack.scalapipeline.akka.AkkaStreamLocalContext._
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import org.json4s.native.Json
import org.json4s.DefaultFormats
import collection.immutable.Iterable


case class TransformationsToFlow(el:TransformationElement) {
    final val log: Logger = LoggerFactory.getLogger(this.getClass)

    def get(): Flow[_,_,NotUsed] ={
        val collectionLevel : Int = el.opt.getOrElse("collectionLevel",0).toString.toInt

        //only lvl1 coll for now
        if (collectionLevel==0) {
            run(el.ttype)
        } else {

            //TODO
            el.ttype match {
                case STREAM_HEAD => {
                    val taketop: Int = el.opt.getOrElse("top", 0).toString.toInt
                    log.info("IN SUB COLLECTION TRANSFO {}", taketop)
                    Flow[Iterable[_]].map(
                        m => m.take(taketop)
                    )

                }
            }
        }
    }

    private def run(tt:TransformationTypeEnum):  Flow[_, _,NotUsed]  = {

        tt match {
            case PARSE_JSON_OBJECT => {
                Flow[ByteString].map(
                    m => play.api.libs.json.Json.parse(m.utf8String)
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
            case ANY_TO_JSON => {
                Flow[AnyRef].map(m => Json(DefaultFormats).write(m))
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
            case STREAM_HEAD => {
                val taketop : Long = el.opt.getOrElse("top", 0).toString.toLong
                if (taketop!=0) {
                    Flow[Any].map(b=>{
                        //DEBUG
                        log.info("TransfoToFLow - Stream head - Bytestring of size "+b.toString.getBytes().length)
                        b
                    }).take(taketop)
                } else {
                    Flow[Any]
                }
            }
            case STREAM_TAIL => {
                log.info("IN STREAM TAIL")
                Flow[Any].reduce((t, t2)=>t2)
            }
            case MOCK_LOG => {
                Flow[Any].map(l=>{log.info("MockTransformation--"+l);l})
            }
        }
    }
}
