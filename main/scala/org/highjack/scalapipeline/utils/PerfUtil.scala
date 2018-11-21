package org.highjack.scalapipeline.utils

import com.google.common.base.Stopwatch
import org.slf4j.{Logger, LoggerFactory}

/**
  * TEMPORARY, FOR DEBUG
  */
object PerfUtil {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)


    var timer:Stopwatch = _
    def initTimer(): Unit = {
        timer = Stopwatch.createStarted()
        logger.info("------ timer started");
    }

    def stopAndLog(): Unit = {
        if(timer==null) {
            logger.error("Timer wasn't started")
            return
        }
        timer.stop()
        timer.elapsed().toMillis
        logger.info("------ Stream completion took  "+timer.elapsed().toMillis+" milliseconds")
        logger.info("------ == "+timer.elapsed().getSeconds+" seconds")
    }


}
