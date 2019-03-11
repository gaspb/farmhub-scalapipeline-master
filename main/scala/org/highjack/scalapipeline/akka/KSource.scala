package org.highjack.scalapipeline.akka

import akka.actor.Actor
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}

@deprecated //handled by AkkaStream
class KSource extends Actor {
    val log : Logger = LoggerFactory.getLogger(this.getClass)
    def receive = {
        case s: Any =>
            log.info("In KSource actor : "+s)
            val reply = ByteString(s.toString)
            sender() ! reply
    }
}
