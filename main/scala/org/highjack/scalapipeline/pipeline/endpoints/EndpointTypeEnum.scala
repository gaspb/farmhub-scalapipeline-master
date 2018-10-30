package org.highjack.scalapipeline.pipeline.endpoints

object EndpointTypeEnum extends Enumeration {
    type EndpointTypeEnum = Value
    val /*FINITE_JSON_LONG_POLL,
        FINITE_BYTESTRING_LONG_POLL,
        FINITE_JSON_READ,

        FINITE_KAFKA_READ,
        FINITE_CASSANDRA_READ,

        INFINITE_JSON_LONG_POLL,
        INFINITE_BYTESTRING_LONG_POLL,
        INFINITE_BYTE_STRE AM,
        INFINITE_JSON_STREAM,
        INFINITE_WEBSOCKET_SUBSCRIBE,*/

        AKKA_REMOTE_STREAM,
        AKKA_HTTP_STREAM_LISTEN,
        AKKA_HTTP_BYTESTRING_READ,
        KAFKA_SUBSCRIBE, //option : compress/decompress bytes (alpakka)
        KAFKA_COMITTABLE_SUBSCRIBE //data shall be rewritten (tranformed) to kafka //even in case of failer, output is "at-least-once"


    /*  FINITE_BYTE_EVENT_DRIVEN,
      FINITE_JSON_EVENT_DRIVEN,
      INFINITE_BYTE_EVENT_DRIVEN,           +event driven from input (the "condition"/branch dividing element) => move to a new PipelineElement
      INFINITE_JSON_EVENT_DRIVEN,*/

    = Value
    def valueOf(name: String) = EndpointTypeEnum.values.find(_.toString == name)
}
