package org.highjack.scalapipeline.scalaThreads

import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import akka.util.ByteString
import org.highjack.scalapipeline.pipeline.{PipelineBranch, PipelineElement, PipelineModel}
import org.highjack.scalapipeline.pipeline.endpoints.EndpointElement
import org.highjack.scalapipeline.pipeline.trigger.TriggerElement
import org.highjack.scalapipeline.utils.PerfUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Future, Promise}
import scala.util.Try


//
import com.google.common.base.Stopwatch

case class LogicBuilder(ppl:PipelineModel) {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher


    private var flow: Flow[ByteString, _, NotUsed]= _
    private var runnableGraph:RunnableGraph[_] = _

    private var result : Any = _

    import LogicBuilder._

    def buildLogicFlow() : LogicBuilder = {
       //ppl.branches //TODO
        val branch0 = ppl.branches.head
        flow = oneBranchFlow(branch0)
        this

    }

    def buildEndpoint() : LogicBuilder = {
        val endpoint:EndpointElement = ppl.endpoint
        runnableGraph = endpointToRunnableGraph(endpoint, flow)
        this
    }

    def setUpTrigger() : LogicBuilder = {
        val trigger:TriggerElement = ppl.trigger
        val futureSource : Source[Future[_], _] = triggerToFuture(trigger)
        futureSource.runWith(Sink.foreach(m => {

            //TODO if m.isCompleted or onComplete





            logger.info("Recieved trigger call, running graph and starting timer")
            PerfUtil.initTimer()
            runnableGraph.async.run()


            logger.info("Result is of type "+result.getClass.getName)
            logger.info("result is "+result)





        }))
        this
    }

}


private object LogicBuilder {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)

    def oneBranchFlow(branch:PipelineBranch): Flow[ByteString, Any, NotUsed] = {
        val flow:Flow[ByteString,Any,NotUsed] = Flow[ByteString]
        branch.elements.foldLeft(flow)((prevFlow, elem)=> {
            logger.info("Folding elem : "+elem.name)
            prevFlow
                .via(elemToFlow(elem))
        })
    }

    def elemToFlow(elem:PipelineElement): Flow[Any, Any, NotUsed] = {
        logger.info("elem to flow : "+elem.name)
        ElemToFlow(elem).get()
    }

    def endpointToRunnableGraph(elem:EndpointElement, logicFlow:Flow[ByteString, _, _]): RunnableGraph[_] = {
        logger.info("adding endpoint : "+elem.name)
        EndpointToRunnableGraph(elem, logicFlow).get()
    }

    def triggerToFuture(elem:TriggerElement): Source[Future[_], _] = {
        logger.info("adding trigger to future : "+elem.name)
        TriggerToFutureSource(elem).trigger()
    }


}
