package com.knoldus

import akka.actor.{Actor, ActorLogging}
import com.knoldus.workerProtocol.Work
//import com.knoldus.workerProtocol.Work

class Worker extends Actor with ActorLogging {
  override def receive={
    case Work(msg) => log.info(s"I recieved work message $msg and my actorRef:${self.path.name}")
  }
  }
object workerProtocol{
  case class Work(name:String)
}
