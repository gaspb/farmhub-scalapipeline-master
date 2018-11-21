package org.highjack.scalapipeline.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, RequestContext, Route}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import org.highjack.scalapipeline.utils.{PerfUtil, StringEncryptionUtil}
import org.slf4j.{Logger, LoggerFactory}


    //TODO import system.dispatcher // to get an implicit ExecutionContext into scope
    object AkkaRestServer extends Directives {
        implicit val system = ActorSystem()
        implicit val materializer = ActorMaterializer()
        // needed for the future flatMap/onComplete in the end
        implicit val executionContext = system.dispatcher
        var exposedOutputsURLMap :collection.mutable.ListMap[String,String] = collection.mutable.ListMap.empty[String,String]
        var exposedInputsURLMap :collection.mutable.ListMap[String,String] = collection.mutable.ListMap.empty[String,String]
        var exposedInputsSourceMap :collection.mutable.ListMap[String,Source[ByteString,_]] = collection.mutable.ListMap.empty[String,Source[ByteString,_]]
        var exposedOutputsSourceMap :collection.mutable.ListMap[String,Source[ByteString,_]] = collection.mutable.ListMap.empty[String,Source[ByteString,_]]
        var exposedTriggerURLMap :collection.mutable.ListMap[String,String] = collection.mutable.ListMap.empty[String,String]
        var exposedTriggerFuncMap :collection.mutable.ListMap[String,() => RunnableGraph[Any]] = collection.mutable.ListMap.empty[String,() => RunnableGraph[Any]]

        def runnablePipelineMap :collection.mutable.ListMap[String,RunnableGraph[Any]] =  collection.mutable.ListMap.empty[String,RunnableGraph[Any]]


        implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
            .withParallelMarshalling(parallelism = 8, unordered = true)
        val logger : Logger = LoggerFactory.getLogger(this.getClass)


        /**
          * GET entities to the url retrieved from POST:getOutputURL {pipelineId:XXX, inputElementName:XXX}
          */
        def exposeOutputAkkaStream(pipelineId:String, outputElementName:String, outputEndpointURL:String, source:Source[ByteString,_]): Unit = {

            val key = pipelineId+" // "+outputElementName

            exposedOutputsURLMap += ((key,outputEndpointURL))
            exposedOutputsSourceMap += ((key,source))
            logger.info("Exposing stream output "+exposedOutputsURLMap.size+" for pipeline "+pipelineId+" and output "+outputElementName+" : url=/"+outputEndpointURL)


        }
        /* val route:Route =
           (get & path(encryptedURL)) {
               logger.info("Completed request "+encryptedURL)
               complete(
                    HttpEntity(ContentTypes.`application/octet-stream`, source2)
               )
           }*/
        /**
          * Post entities to the url retrieved from POST:getInputURL {pipelineId:XXX, inputElementName:XXX}
         */
        def exposeInputAkkaStream(pipelineId:String, inputElementName:String, callBack:(Source[ByteString,_])=>_): Unit = {
            val encryptedURL : String = StringEncryptionUtil.encrypt(pipelineId+" // "+inputElementName)

            //TODO unmarshalling protocol
          //  Source.from
            logger.info("Exposing stream endpoint for pipeline "+pipelineId+" and endpoint "+inputElementName+" : url=/"+encryptedURL)
            val route:Route =
            (post & path(encryptedURL)) {
                logger.info("Completed request "+encryptedURL)
                entity(asSourceOf[ByteString]) { k =>
                    callBack(k)
                    complete(HttpResponse())
                }
            }

        }

        def exposeTrigger(pipelineId:String, triggerName:String, outputEndpointURL:String,  run: () => RunnableGraph[Any]): Unit = {
            val key = pipelineId+" // "+triggerName

            exposedTriggerURLMap += ((key,outputEndpointURL))
            exposedTriggerFuncMap += ((key,run))
            logger.info("Exposing stream trigger "+exposedTriggerURLMap.size+" for pipeline "+pipelineId+" and trigger "+triggerName+" : url=/"+outputEndpointURL)



        }

        //ESXPOSE STREAM JSON => NEED PROTOCOL
       /* val response = HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, input))

        val value: Source[Tweet, Any] =
            response.entity.dataBytes
                .via(jsonStreamingSupport.framingDecoder) // pick your Framing (could be "\n" etc)
                .mapAsync(1)(bytes ⇒ Unmarshal(bytes).to[Tweet]) // unmarshal one by one*/
    }
