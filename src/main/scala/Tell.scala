import akka.actor.{Actor, ActorLogging, ActorSystem, Kill, PoisonPill, Props}
import akka.util.Timeout
import akka.pattern._

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._


class Tell extends Actor with ActorLogging{
def receive ={
  case  msg if msg=="hello" => log.info(s"Message sent=$msg")
    sender ! true
  case _ => log.info("invalid message")
    //sender ! false

}
}

object Tell extends App {
  val system = ActorSystem("TellPatternSystem")
  val tellPattern = system.actorOf(Props[Tell], "Tell")

  tellPattern ! "hello"
 // system.terminate()
  tellPattern ! PoisonPill
  tellPattern ! "hi"
  /* implicit val timeout = Timeout(5 second)

  val actorFound: Future[Boolean]= (tellPattern ? "hello").mapTo[Boolean]
  for {
    found <- actorFound
  } yield println(s"Found = $found")
*/
}