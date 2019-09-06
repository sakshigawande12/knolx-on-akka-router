package com.knoldus

import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor._
import akka.routing.{DefaultResizer, ScatterGatherFirstCompletedPool}
import akka.util.Timeout
import com.knoldus.CommunicationProtocol.{CheckNoOfMessages, WorkerFailedException}

import scala.concurrent.Future
import scala.language.postfixOps

object CommunicationProtocol {
  case class Message(name: String)

  case class CheckNoOfMessages(name: String)

  case class WorkerFailedException(error: String) extends Exception(error)
}

object ScttetGatherFirstCompletePool extends App {

  val system = ActorSystem("CommunicationActorSystem")
  val donutStockActor = system.actorOf(Props[CommunicationActor], name = "CommunicationActor")

  implicit val timeout = Timeout(5 seconds)

  val messageCount = (1 to 2).map(i => (donutStockActor ? CheckNoOfMessages("Hello")).mapTo[Int])
  for {
    count <- Future.sequence(messageCount)
  } yield println(s"Hello message count = $count")

  Thread.sleep(5000)

  val isTerminated = system.terminate()
  class CommunicationActor extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 5 seconds) {
        case _: WorkerFailedException =>
          log.error("Worker failed exception, will restart.")
          Restart

        case _: Exception =>
          log.error("Worker failed, will need to escalate up the hierarchy")
          Escalate
      }

    val workerName = "CommunicationWorkerActor"
    val resizer = DefaultResizer(lowerBound = 5, upperBound = 10)
    val props = ScatterGatherFirstCompletedPool(     //TailChoppingPool
      nrOfInstances = 5,
      resizer = Some(resizer),
      supervisorStrategy = supervisorStrategy,
      within = 5 seconds
    ).props(Props[CommunicationWorkerActor])

    val communicationWorkerRouterPool: ActorRef = context.actorOf(props, "CommunicationWorkerRouter")

    def receive = {
      case checkStock @ CheckNoOfMessages(name) =>
        log.info(s"check number of message of $name ")
        communicationWorkerRouterPool forward checkStock
    }
  }


  class CommunicationWorkerActor extends Actor with ActorLogging {

    override def postRestart(reason: Throwable): Unit = {
      log.info(s"restarting ${self.path.name} because of $reason")
    }

    def receive = {
      case CheckNoOfMessages(name) =>
        val total=sender ! findCount(name)
        log.info(s"Finding number of count of message  $name, $total  thread = ${Thread.currentThread().getId}")
    }

    def findCount(name: String): Int = {

      100
    }
  }
}
