package com.knoldus

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}
import com.knoldus.workerProtocol.Work

import akka.io.Udp.SO.Broadcast
import com.knoldus.Worker

class Router extends Actor with ActorLogging{
  var routees: List[ActorRef] = _

  override def preStart()={
    routees = List.fill(5){
      context.actorOf(Props[Worker])
    }
  }

  override def receive={
    case msg:Work => log.info("I m a router and i recieved a message...")
      routees(util.Random.nextInt(routees.size))forward(msg)

  }

}

/* class RouterGroup( routees : List[String])extends Actor with ActorLogging {
  override def receive = {
    case msg: Work => log.info("I m a router and i recieved a message...")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward (msg)
  }
}
 */

object  Router extends App{
val system = ActorSystem("Router")
  //system.actorOf(Props[Worker],name = "worker1")
  //system.actorOf(Props[Worker],name = "worker2")
  //system.actorOf(Props[Worker],name = "worker3")

/*  val workers :List[String]=List("/user/worker1",
    "/user/worker2",
    "/user/worker3")

  val routerGroup= system.actorOf(Props(classOf[RouterGroup],workers))

  routerGroup ! Work()
  routerGroup ! Work() */

  val router= system.actorOf(Props(classOf[Router]))
  router ! Work("hi")
  router ! Work("hello")



  system.terminate()
}