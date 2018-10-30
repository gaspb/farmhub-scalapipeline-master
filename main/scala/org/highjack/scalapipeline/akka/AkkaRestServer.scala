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
import org.highjack.scalapipeline.utils.StringEncryptionUtil
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

        def runnablePipelineMap :collection.mutable.ListMap[String,RunnableGraph[Any]] =  collection.mutable.ListMap.empty[String,RunnableGraph[Any]]


        implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
            .withParallelMarshalling(parallelism = 8, unordered = true)
        val logger : Logger = LoggerFactory.getLogger(this.getClass)


        /**
          * GET entities to the url retrieved from POST:getOutputURL {pipelineId:XXX, inputElementName:XXX}
          */
        def exposeOutputAkkaStream(pipelineId:String, outputElementName:String, source:Source[ByteString,_]): Unit = {

            val key = pipelineId+" // "+outputElementName
            val encryptedURL : String = StringEncryptionUtil.encrypt(key)

            exposedOutputsURLMap += ((key,encryptedURL))
            exposedOutputsSourceMap += ((key,source))
            logger.info("Exposing stream output "+exposedOutputsURLMap.size+" for pipeline "+pipelineId+" and output "+outputElementName+" : url=/"+encryptedURL)


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


     /*   implicit val materializer = ActorMaterializer()
        implicit val system = ActorSystem()
        implicit val executionContext =system.dispatcher

        case class DataToUpload[T](key: String, value: /*T*/AnyRef)

        def dataToUpload[T](): Source[DataToUpload[T], NotUsed] =
        // This could even be a lazy/infinite stream. For this example we have a finite one:
            Source(List(
                DataToUpload[T]("key1",1),
                DataToUpload[T]("key2",2),
                DataToUpload[T]("key3",3)
            ))

        val poolClientFlow =
            Http().cachedHostConnectionPool[DataToUpload[Any]]("akka.io")//TODO

        def createUploadRequest(dataToUpload: DataToUpload): Future[(HttpRequest, FileToUpload)] = {
            val bodyPart =
            // fromPath will use FileIO.fromPath to stream the data from the file directly
                FormData.BodyPart.fromPath(dataToUpload.key, ContentTypes.`application/octet-stream`, dataToUpload.value)

            val body = FormData(bodyPart) // only one file per upload
            Marshal(body).to[RequestEntity].map { entity => // use marshalling to create multipart/formdata entity
                // build the request and annotate it with the original metadata
                HttpRequest(method = HttpMethods.POST, uri = "http://example.com/uploader", entity = entity) -> fileToUpload
            }
        }

        // you need to supply the list of files to upload as a Source[...]
        filesToUpload()
            // The stream will "pull out" these requests when capacity is available.
            // When that is the case we create one request concurrently
            // (the pipeline will still allow multiple requests running at the same time)
            .mapAsync(1)(createUploadRequest)
            // then dispatch the request to the connection pool
            .via(poolClientFlow)
            // report each response
            // Note: responses will not come in in the same order as requests. The requests will be run on one of the
            // multiple pooled connections and may thus "overtake" each other.
            .runForeach {
            case (Success(response), fileToUpload) =>
                // TODO: also check for response status code
                println(s"Result for file: $fileToUpload was successful: $response")
                response.discardEntityBytes() // don't forget this
            case (Failure(ex), fileToUpload) =>
                println(s"Uploading file $fileToUpload failed with $ex")
        }*/



        //ESXPOSE STREAM JSON => NEED PROTOCOL
       /* val response = HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, input))

        val value: Source[Tweet, Any] =
            response.entity.dataBytes
                .via(jsonStreamingSupport.framingDecoder) // pick your Framing (could be "\n" etc)
                .mapAsync(1)(bytes ⇒ Unmarshal(bytes).to[Tweet]) // unmarshal one by one*/
    }
