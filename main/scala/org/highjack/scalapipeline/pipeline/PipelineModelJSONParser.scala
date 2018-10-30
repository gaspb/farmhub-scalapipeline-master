package org.highjack.scalapipeline.pipeline


import org.highjack.scalapipeline.pipeline.PipelineElementTypeEnum._
import org.highjack.scalapipeline.pipeline.endpoints.{EndpointElement, EndpointTypeEnum}
import org.highjack.scalapipeline.pipeline.endpoints.EndpointTypeEnum.EndpointTypeEnum
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputTypeEnum}
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum.OutputTypeEnum
import org.highjack.scalapipeline.pipeline.transformations.{TransformationElement, TransformationTypeEnum}
import org.highjack.scalapipeline.pipeline.transformations.TransformationTypeEnum.TransformationTypeEnum
import org.highjack.scalapipeline.pipeline.trigger.{TriggerElement, TriggerTypeEnum}
import org.highjack.scalapipeline.pipeline.trigger.TriggerTypeEnum.TriggerTypeEnum
import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
  * Created by High Jack on 28/10/2018.
  */
object PipelineModelJSONParser {

    def parse(str:String): PipelineModel = {
        Json.parse(str).as[PipelineModel]
    }




    implicit val tsEnumRds  : Reads[TransformationTypeEnum.Value] = enumReads(TransformationTypeEnum)
    implicit val outEnumRds  : Reads[OutputTypeEnum.Value] = enumReads(OutputTypeEnum)
    implicit val edpEnumRds  : Reads[EndpointTypeEnum.Value] = enumReads(EndpointTypeEnum)
    implicit val tgEnumRds  : Reads[TriggerTypeEnum.Value] = enumReads(TriggerTypeEnum)
    implicit val elEnumRds  : Reads[PipelineElementTypeEnum.Value] = enumReads(PipelineElementTypeEnum)

    implicit val tsElemRds : Reads[TransformationElement] = (
        (__ \ "name").read[String] ~
            (__ \ "ttype").read[TransformationTypeEnum.Value]/*((s:JsValue)=> s.validate[String].map(s=>TransformationTypeEnum.valueOf(s).get))*/ ~
            (__ \ "opt").read[Map[String,String]]
        )(TransformationElement)
    implicit val endpElemRds : Reads[EndpointElement] = (
        (__ \ "name").read[String] ~
            (__ \ "endpointType").read[EndpointTypeEnum.Value]/*((s:JsValue)=> s.validate[String].map(s=>EndpointTypeEnum.valueOf(s).get))*/ ~
            (__ \ "address").read[String] ~
            (__ \ "port").read[Int] ~
            (__ \ "kafkaInputKey").read[String] ~
            (__ \ "options").read[Map[String,String]]
        )(EndpointElement)
    implicit val outElemRds : Reads[OutputElement] = (
        (__ \ "name").read[String] ~
            (__ \ "outputEndpointURL").read[String] ~
            (__ \ "otype").read[OutputTypeEnum.Value]/*((s:JsValue)=> s.validate[String].map(s=>OutputTypeEnum.valueOf(s).get))*/
        )(OutputElement)
    implicit val trigElemRds : Reads[TriggerElement] = (
        (__ \ "name").read[String] ~
            (__ \ "outputEndpointURL").read[String] ~
            (__ \ "ttype").read[TriggerTypeEnum.Value]/*((s:JsValue)=> s.validate[String].map(s=>TriggerTypeEnum.valueOf(s).get))*/
        )(TriggerElement)


    implicit val elemRds : Reads[PipelineElement] = elemReads()


    implicit val branchRds : Reads[PipelineBranch] = (
        (__ \ "elements").read[Set[PipelineElement]] ~
            (__ \ "branchId").read[Long] ~
            (__ \ "parentBranchId").read[Long] ~
            (__ \ "startIdxAtParent").read[Int]
        )(PipelineBranch)

    implicit val pplRds : Reads[PipelineModel] = (
        (__ \ "branches").read[Set[PipelineBranch]] ~
            (__ \ "pipelineId").read[String] ~
            (__ \ "endpoint").read[EndpointElement] ~
            (__ \ "trigger").read[TriggerElement]
        )(PipelineModel)







    def enumReads[E <: Enumeration] (enum: E) : Reads[E#Value] = new Reads[E#Value] {
        def reads(json: JsValue): JsResult[E#Value] = json match {
            case JsString(s) => {
                try {
                    JsSuccess(enum.withName(s))
                } catch {
                    case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
                }
            }
            case _ => JsError("String value expected")
        }
    }
    def elemReads[E >:PipelineElement] () : Reads[E] = new Reads[E] {
        def reads(json: JsValue): JsResult[E] = {
            val elemType = (json \ "elementType").as[PipelineElementTypeEnum]
            elemType match {
                case ENDPOINT => JsSuccess(json.as[EndpointElement])
                case OUTPUT => JsSuccess(json.as[OutputElement])
                case TRIGGER => JsSuccess(json.as[TriggerElement])
                case TRANSFORMATION => JsSuccess(json.as[TransformationElement])
            }

        }
    }

}
