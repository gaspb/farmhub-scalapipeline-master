package org.highjack.scalapipeline.scalaTransformation


/**
  * EXAMPLE INTERFACE FOR A USER-DEFINED REDUCE FUNCTION
  */
abstract class Reduce extends ScalaTransformation {

    def transformationId = "reduce1"
    def run(rawData : Object, args: AnyRef):Object = {

        var refinedData = rawData

        /** BEGIN userCode */









        /** END userCode */

        refinedData
    }

}
