package org.highjack.scalapipeline.akka

import akka.actor.{Actor, ActorContext}
import akka.util.ByteString

/**
  * Created by High Jack on 30/10/2018.
  */
class TCPServerActor extends Actor {


    override def receive: Receive = {
        case s:ByteString => {
            ""
        }
    }

}
