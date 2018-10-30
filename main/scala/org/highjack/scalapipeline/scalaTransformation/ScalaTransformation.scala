package org.highjack.scalapipeline.scalaTransformation

import akka.stream.scaladsl.Source


abstract class ScalaTransformation[A,B] {

    def run(rawData : Source[A, _], args: Option[Any]):Source[B, _]

}
