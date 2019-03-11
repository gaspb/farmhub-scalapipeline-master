package org.highjack.scalapipeline.executor

import java.util.concurrent.Executors

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.JavaConversions.asExecutionContext
import org.highjack.scalapipeline.pipeline.PipelineModel
import org.highjack.scalapipeline.stream.ExecutedPipelineTask

import scala.concurrent.{ExecutionContext, Future}

case class PipelineTaskExecutor() {
    protected implicit val context:ExecutionContext =
        asExecutionContext(Executors.newSingleThreadExecutor())
    final val log: Logger = LoggerFactory.getLogger(this.getClass)
    var activeThreads : Map[String, ExecutedPipelineTask] = Map.empty[String, ExecutedPipelineTask]

    def run(pipelineId: String, userKey: String, scalaPipeline: PipelineModel, kafkaTopic : String): String = {
        val scalaThread = new ExecutedPipelineTask(pipelineId, userKey, scalaPipeline)
        activeThreads += ((pipelineId+"//"+userKey, scalaThread))
        Future(scalaThread.start) //async
        ""
    }

}
