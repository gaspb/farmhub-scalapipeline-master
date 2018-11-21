package org.highjack.scalapipeline.pipeline.transformations

object TransformationTypeEnum extends Enumeration {
    type TransformationTypeEnum = Value
    val GENERIC_CONVERSION,
        DIVIDE_STREAM,
        //JSON-related
        PARSE_JSON_OBJECT, //jsonDelimiter:Boolean
        PARSE_GENERIC_CLASS,
    RETRIEVE_PROPERTY_FROM_JSON_AS_JSON,
    RETRIEVE_PROPERTY_FROM_JSON_AS_STRING,
        RETRIEVE_PROPERTY_MAP_FROM_JSON,
        REDUCE_JSON_OBJECT,
        //STREAM-related
        STREAM_HEAD,
        STREAM_TAIL,
    //will run following pipeline elements on each element on the
        TO_SOURCE,
        FROM_SOURCE,
        //SPARK
        LINEAR_REGRESSION,

      //common conversions
        STRING_TO_BYTESTRING,
        BYTESTRING_TO_STRING,
        ANY_TO_BYTESTRING,
        ANY_TO_JSON,

        MOCK_LOG,//mock


        //CUSTOM
        OPERATION_BASED, //operation made with the app
        WORD_OCCURENCE_COUNT //excludeCommonWords:Boolean



         = Value
    def valueOf(name: String) = TransformationTypeEnum.values.find(_.toString == name)
}
