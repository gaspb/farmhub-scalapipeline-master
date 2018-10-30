package org.highjack.scalapipeline.scalaTransformation

abstract class Reduce extends ScalaTransformation {

    def transformationId = "reduce1"
    def run(rawData : Object, args: AnyRef):Object = {

        var refinedData = rawData

        /** BEGIN userCode */









        /** END userCode */

        refinedData
    }

}
