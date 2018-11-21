package org.highjack.scalapipeline.akka


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, StreamConverters}
import akka.util.ByteString
import com.google.common.io.BaseEncoding
import org.highjack.scalapipeline.pipeline.{JsonPipelineVM, PipelineModelJSONParser}
import org.highjack.scalapipeline.scalaThreadExecutor.ScalaThreadExecutor
import org.highjack.scalapipeline.utils.{Java8Util, PerfUtil}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation._
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import scala.concurrent.Future
@Controller //?
@RestController
@RequestMapping(Array("/api/test"))
class AkkaRestController {

        val logger : Logger= LoggerFactory.getLogger(this.getClass)
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    @RequestMapping(value = Array("/ppl"), method = Array(RequestMethod.POST), consumes = Array(MediaType.APPLICATION_JSON_VALUE))
    def postPipeline(@RequestBody jsonPipeline: JsonPipelineVM): ResponseEntity[Any]= {
       val str =  ByteString(BaseEncoding.base64().decode(jsonPipeline.getBase64())).utf8String
        logger.info("---------- recieved : "+str)
        val pplModel = PipelineModelJSONParser.parse(str)
        ScalaThreadExecutor().run("ppl1","user1", pplModel, "topic1")
        logger.info("---------- RETURNING : "+true)
        new ResponseEntity[Any](true, HttpStatus.OK)
    }


    @GetMapping(path = Array("/out"),  produces = Array(MediaType.TEXT_PLAIN_VALUE))
        @ResponseBody
        def getOutputURL(@RequestParam pipelineId: String, @RequestParam outputName: String): String= {
            logger.info("Received call for url /api/out, retriving from map with length " + AkkaRestServer.exposedOutputsURLMap.size + "  ")
            val str:String = Java8Util.get(pipelineId + " // " + outputName, AkkaRestServer.exposedOutputsURLMap)
            str
        }

        @GetMapping(path = Array("/in"), produces = Array(MediaType.TEXT_PLAIN_VALUE))
        @ResponseBody
        def getInputURL(@RequestParam pipelineId: String, @RequestParam inputName: String): String = {
            logger.info("Received call for url /api/out" + AkkaRestServer.exposedInputsURLMap.size + "  ")
            val str:String = Java8Util.get(pipelineId + " // " + inputName, AkkaRestServer.exposedInputsURLMap)
            str
        }


    //return a bytebuffer with last data from the source in it
    //retrieve json map with var d = atob(data).substring(4,a.length-2) then according to datatype (here a map) .split(', ').map(elem=>elem.split(' -> '))
    @GetMapping(path = Array("/proxy/stream_json/{url}"), produces = Array(MediaType.APPLICATION_STREAM_JSON_VALUE))
    @ResponseBody
    def proxyStreamJSON(@PathVariable("url") url: String): Flux[ByteString]  = {
        logger.info("Received call for proxy " + url + "  , lgth="+AkkaRestServer.exposedOutputsSourceMap.size)
        val key = Java8Util.getKeyFromValue(url, AkkaRestServer.exposedOutputsURLMap)
        logger.info("Retrieved key " + key )
        val source : Source[ByteString, _]= Java8Util.get[Source[ByteString, _]](key, AkkaRestServer.exposedOutputsSourceMap)
        //get a materialized source

        //   val sink = StreamConverters.asInputStream()
        val flux : Flux[ByteString] = Flux.fromStream(source.runWith(StreamConverters.asJavaStream[ByteString]()))
        flux
    }

    @GetMapping(path = Array("/proxy/run/{url}"), produces = Array(MediaType.APPLICATION_STREAM_JSON_VALUE))
    @ResponseBody
    def proxyRunJSON(@PathVariable("url") url: String): String  = {
        logger.info("Received call for proxy " + url + "  , lgth="+AkkaRestServer.exposedTriggerFuncMap.size)
        val key = Java8Util.getKeyFromValue(url, AkkaRestServer.exposedTriggerURLMap)
        logger.info("Retrieved key " + key )
        val runnable : () => RunnableGraph[Any]= Java8Util.get[() => RunnableGraph[Any]](key, AkkaRestServer.exposedTriggerFuncMap)
        PerfUtil.initTimer()
        Future {runnable().run()}
        "200"
    }


    //TODO
    /**
      *
      * On call to proxy/url
      * retrieve the Runnable operation to
      *
      *
      *
      *
      *
      *
      *
      */


}
