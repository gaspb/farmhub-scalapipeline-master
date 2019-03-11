package org.highjack.scalapipeline.interfaces

/**
  * EXAMPLE INTERFACE FOR A USER-DEFINED REDUCE FUNCTION
  */
abstract class Reduce extends GenericTransformation {
    def transformationId = "reduce1"
    def run(rawData : Object, args: AnyRef):Object = {

        var refinedData = rawData

        /** BEGIN userCode */








        /** END userCode */

        refinedData
    }

}
