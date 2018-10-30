package org.highjack.scalapipeline.web.rest;

import akka.http.javadsl.model.ContentTypes;
import akka.stream.scaladsl.Source;
import akka.util.ByteString;
import com.codahale.metrics.annotation.Timed;
import com.google.common.io.BaseEncoding;
import org.highjack.scalapipeline.akka.AkkaRestServer;
import org.highjack.scalapipeline.pipeline.PipelineModel;
import org.highjack.scalapipeline.pipeline.PipelineModelJSONParser;
import org.highjack.scalapipeline.scalaThreadExecutor.ScalaThreadExecutor;
import org.highjack.scalapipeline.utils.Java8Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scala.Function1;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;

import java.util.Map;

/**
 * Controller for view and managing Log Level at runtime.
 *
 * PATH MUST BE LOWERCASE !!!
 */

public class ApiResource {




  /*  Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostMapping("/out")
    @Timed
    public ResponseEntity<String> getOutputURL3(@RequestParam String pipelineId, @RequestParam String outputName) {
        String str = Java8Util.get(pipelineId + " // " + outputName, AkkaRestServer.exposedOutputsURLMap());
        return new ResponseEntity<>(str, HttpStatus.OK);
    }

    @PostMapping("/in")
    @Timed
    public ResponseEntity<String> getInputURL(@RequestParam String pipelineId, @RequestParam String inputName) {
        String str = Java8Util.get(pipelineId + " // " + inputName, AkkaRestServer.exposedInputsURLMap());
        return new ResponseEntity<>(str, HttpStatus.OK);
    }
    @RequestMapping("/proxy/{url}")
    @Timed
    public ResponseEntity<Source<ByteString, ?>> getProxy(@PathVariable("url") String url) {
        String key = Java8Util.getKeyFromValue(url, AkkaRestServer.exposedOutputsURLMap());

        Source<ByteString, ?> source = Java8Util.get(key, AkkaRestServer.exposedOutputsSourceMap());
       int debug =  Java8Util.debugSource(source);
        logger.info("Received call for proxy "+key+" , exposing source with lgth " +debug);
        return new ResponseEntity<Source<ByteString, ?>>(source, HttpStatus.OK);
    }

*/
}
