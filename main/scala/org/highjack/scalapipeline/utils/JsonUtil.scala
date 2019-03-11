package org.highjack.scalapipeline.utils

import play.api.libs.json.{Json, Reads, Writes}

object JsonUtil {

    def serialize[K <:AnyRef](obj:K)(implicit w:Writes[K]): String = {
        Json.stringify(Json.toJson(obj))
    }
    def deserialize[K<:AnyRef](jsonStr:String)(implicit r:Reads[K]): K = {
        Json.parse(jsonStr).validate[K].get
    }




}
