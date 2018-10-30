package org.highjack.scalapipeline.scalaThreads

import java.nio.file.Paths

import akka.NotUsed
import akka.io.IO
import akka.stream.scaladsl.{FileIO, Flow, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.akka.AkkaRestServer
import org.highjack.scalapipeline.pipeline.outputs.{OutputElement, OutputMethods}
import org.highjack.scalapipeline.pipeline.outputs.OutputTypeEnum._
import org.highjack.scalapipeline.pipeline.transformations.StringSourceToStatsMethods
import org.highjack.scalapipeline.scalaThreads.LogicBuilder.logger
import play.api.libs.json.{JsValue, Json}

/**
  * Created by High Jack on 28/10/2018.
  */
case class OutputsToFlow(el:OutputElement) {

    def get(): Flow[_,_,NotUsed] ={
        el.otype match {
            case TO_AKKA_REMOTE_TCP => {
                ???

            }
            case TO_DOWNLOADABLE_FILE => {
                val filename = el.outputEndpointURL
                val flow = Flow[Any]
                      .map(_.toString)
                    .map(s => ByteString(s + "\n"))
                    .alsoTo(FileIO.toPath(Paths.get(filename)))
                logger.info("Output to flow : "+filename)
                AkkaRestServer.exposeOutputAkkaStream("todo", el.name, FileIO.fromPath(Paths.get(filename)))

                flow
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
