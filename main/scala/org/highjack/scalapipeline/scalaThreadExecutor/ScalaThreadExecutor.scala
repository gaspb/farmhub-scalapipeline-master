package org.highjack.scalapipeline.scalaThreadExecutor

import java.util.concurrent.Executors

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.JavaConversions.asExecutionContext
import org.highjack.scalapipeline.pipeline.PipelineModel
import org.highjack.scalapipeline.scalaThreads.ScalaThread

import scala.concurrent.{ExecutionContext, Future}

case class ScalaThreadExecutor() {
    protected implicit val context:ExecutionContext =
        asExecutionContext(Executors.newSingleThreadExecutor())
    final val log: Logger = LoggerFactory.getLogger(this.getClass)
    var activeThreads : Map[String, ScalaThread] = Map.empty[String, ScalaThread]

    def run(pipelineId: String, userKey: String, scalaPipeline: PipelineModel, kafkaTopic : String): String = {
        val scalaThread = new ScalaThread(pipelineId, userKey, scalaPipeline)
        activeThreads += ((pipelineId+"//"+userKey, scalaThread))
        Future(scalaThread.start) //async
        ""
    }

}
