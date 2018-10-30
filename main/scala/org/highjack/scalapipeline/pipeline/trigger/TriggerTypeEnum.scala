package org.highjack.scalapipeline.pipeline.trigger

//Run the pipeline part.
object TriggerTypeEnum extends Enumeration {
    type TriggerTypeEnum = Value

    val FROM_REST_ENDPOINT,
        SHEDULED,
        WHILE,
        SIMPLE_RUN

        = Value


/*    val AGGREGATE_COPY_DATA_RUN_ENDPOINT,//copy data from the source to a new (via a configured buffer), run the first and pass the second
        AGGREGATE_RUN_RESULT_ENDPOINT,//run the pipeline and pass result as a new source
        DEAD_END_RUN, //source.to(Sink.ignore).run()
        AGGREGATE //result from source.to(Sink.ignore).run() is holded in a buffer until a threshold is reached, or until another trigger element run the upfront stream itself
    //AKKA SINK THAT RUN THE PPL OR SINK.IGNORE TO RUNNABLE AND EXPOSE ENDPOINT
         = Value*/
    def valueOf(name: String) = TriggerTypeEnum.values.find(_.toString == name)
}
