package org.highjack.scalapipeline.stream

import java.nio.file.Paths

import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink}
import akka.util.ByteString
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum.{TO_AKKA_REMOTE_TCP, TO_DOWNLOADABLE_FILE, TO_KAFKA, TO_REST_ENDPOINT}
import org.highjack.scalapipeline.pipeline.outputs.OutputElement


//NOT USED, => OutputToFlow instead
@deprecated
case class OutputToSink(el:OutputElement) {

    def get(): Sink[_,_] ={
        el.otype match {
            case TO_AKKA_REMOTE_TCP => {
                ???
            }
            case TO_DOWNLOADABLE_FILE => {
                val filename = el.outputEndpointURL
                Flow[String]
                    .map(s => ByteString(s + "\n"))
                    .to(FileIO.toPath(Paths.get(filename.get)))
            }
            case TO_KAFKA => {
                ???
            }
            case TO_REST_ENDPOINT => {
                ???
            }


        }

    }



}
