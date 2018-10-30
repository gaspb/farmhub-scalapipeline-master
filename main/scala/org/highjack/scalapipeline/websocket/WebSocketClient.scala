package org.highjack.scalapipeline.websocket

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.{ActorMaterializer, FlowShape, KillSwitches, SourceShape}
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}

import scala.concurrent.{ExecutionContext, Future}

object WebSocketClient {
 /* def apply(id: String, endpoint: String, supervisor: ActorRef)
           (implicit
            system: ActorSystem,
            materializer: ActorMaterializer,
            executionContext: ExecutionContext) = {
    new WebSocketClient(id, endpoint, supervisor)(system, materializer, executionContext)
  }
}

class WebSocketClient(id: String, endpoint: String, supervisor: ActorRef)
                     (implicit
                      system: ActorSystem,
                      materializer: ActorMaterializer,
                      executionContext: ExecutionContext) {
  val webSocket: Flow[Message, Message, Future[WebSocketUpgradeResponse]] = {
    val websocketUri = s"$endpoint/$id"
    Http().webSocketClientFlow(WebSocketRequest(websocketUri))
  }

  val outgoing = GraphDSL.create() { implicit builder =>
    val data = WindTurbineData(id)

    val flow = builder.add {
      Source.tick(1 seconds, 1 seconds, ())
        .map(_ => TextMessage(data.getNext))
    }

    SourceShape(flow.out)
  }

  val incoming = GraphDSL.create() { implicit builder =>
    val flow = builder.add {
      Flow[Message]
        .collect {
          case TextMessage.Strict(text) =>
            Future.successful(text)
          case TextMessage.Streamed(textStream) =>
            textStream.runFold("")(_ + _)
              .flatMap(Future.successful)
        }
        .mapAsync(1)(identity)
        .map(println)
    }

    FlowShape(flow.in, flow.out)
  }

  val ((upgradeResponse, killSwitch), closed) = Source.fromGraph(outgoing)
    .viaMat(webSocket)(Keep.right) // keep the materialized Future[WebSocketUpgradeResponse]
    .viaMat(KillSwitches.single)(Keep.both) // also keep the KillSwitch
    .via(incoming)
    .toMat(Sink.ignore)(Keep.both) // also keep the Future[Done]
    .run()

  val connected =
    upgradeResponse.map { upgrade =>
      upgrade.response.status match {
        case StatusCodes.SwitchingProtocols => supervisor ! Upgraded
        case statusCode => supervisor ! FailedUpgrade(statusCode)
      }
    }

  connected.onComplete {
    case Success(_) => supervisor ! Connected
    case Failure(ex) => supervisor ! ConnectionFailure(ex)
  }

  closed.map { _ =>
    supervisor ! Terminated
  }*/
}
