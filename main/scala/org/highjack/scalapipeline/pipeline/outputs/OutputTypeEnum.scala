package org.highjack.scalapipeline.pipeline.outputs

object OutputTypeEnum extends Enumeration {
    type OutputTypeEnum = Value
    val TO_DOWNLOADABLE_FILE,
        TO_REST_ENDPOINT,
        TO_KAFKA,
        TO_AKKA_REMOTE_TCP,
        WEBSOCKET,//depr => kafka
        MOCK_TCP

         = Value
    def valueOf(name: String) = OutputTypeEnum.values.find(_.toString == name)
}
