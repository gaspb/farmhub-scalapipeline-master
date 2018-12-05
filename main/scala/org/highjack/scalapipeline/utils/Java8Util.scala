package org.highjack.scalapipeline.utils

import java.util.function.Consumer

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.Source
import org.slf4j.{Logger, LoggerFactory}


object Java8Util {
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
     def toJavaConsumer[T](consumer: (T) => Unit): Consumer[T] ={
        new Consumer[T] {
            override def accept(t: T): Unit = {
                consumer(t)
            }
        }
    }
    def getKeyFromValue(value:String, map:collection.mutable.Map[String,String]): String = {
        val s = map.find(p=>p._2.equals(value))
        if (s.isDefined)
            s.get._1
        else
            "not_found"

    }
    def get[K](key:String, map:collection.mutable.Map[String,K]) : K = {
        logger.info("getting key "+key+" from map ",map.toString)
        val source = map(key)
        logger.info("retrieved ",source.toString)
        map(key)

    }

    def pushToActor(actorRef:ActorRef,message:String): Unit = {
        logger.info("pushing to actor ref ")
        actorRef ! message
    }

    /*//Materialize source
        def debugSource(source:Source[ByteString,_]) : Int ={
            var int:Int = 0

            source.runWith(Sink.fold(ByteString.empty)((prev, curr)=> {
                logger.info("DEBUG--returning "+curr)
                int+=1
                curr
            }))
            int

        }*/
}
