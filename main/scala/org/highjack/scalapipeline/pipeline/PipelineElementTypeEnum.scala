package org.highjack.scalapipeline.pipeline




object PipelineElementTypeEnum extends Enumeration {
    type PipelineElementTypeEnum = Value
    val ENDPOINT,
        TRANSFORMATION,
        MODEL_TRAINING,
        TRANSACTION,
        OUTPUT,
        TRIGGER,
        BRANCH = Value
    def valueOf(name: String) = PipelineElementTypeEnum.values.find(_.toString == name)
}
