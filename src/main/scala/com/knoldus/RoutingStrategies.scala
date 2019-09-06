package com.knoldus

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{Broadcast, RoundRobinPool}

class RoutingStrategies extends Actor with ActorLogging {
  override def receive = {
    case msg: String => log.info(s" I am $msg,${self.path.name}")
    case _ => log.info("Unknown message ")
  }

}

object RoutingStrategies extends App {
  val actorSystem = ActorSystem("Akka-RoundRobin-Example")
  val roundRobinRouter =
    actorSystem.actorOf(RoundRobinPool(2).props(Props[RoutingStrategies]))
  for (i <- 1 to 4) {
    roundRobinRouter ! "hello"
    roundRobinRouter ! Broadcast("hey")
  }

  /*val broadcastRouter =
    actorSystem.actorOf(BroadcastPool(3).props(Props[RouterActor]))
  for (i <- 1 to 4) {
    broadcastRouter ! "hello"
  }
*/
  /* val tailChoppingRouter =
   actorSystem.actorOf(TailChoppingPool(nrOfInstances = 3,
     //resizer = Some(resizer),
     within = 5 seconds,
     interval = 10 millis).props(Props[RouterActor]))
   for (i <- 1 to 4) {
     tailChoppingRouter ! "hello"

   }*/
}