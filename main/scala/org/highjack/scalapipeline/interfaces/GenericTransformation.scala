package org.highjack.scalapipeline.interfaces

import akka.stream.scaladsl.Source

abstract class GenericTransformation[A,B] {
    def run(rawData : Source[A, _], args: Option[Any]):Source[B, _]

}
