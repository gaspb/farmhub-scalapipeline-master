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
    }

    def stopAndLog(): Unit = {
        timer.stop()
        timer.elapsed().toMillis
        logger.info("------ Stream completion took  "+timer.elapsed().toMillis+" milliseconds")
        logger.info("------ == "+timer.elapsed().getSeconds+" seconds")
    }


}
