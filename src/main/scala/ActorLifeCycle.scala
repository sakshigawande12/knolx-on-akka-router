import akka.actor.{Actor, ActorLogging, ActorSystem, Props,PoisonPill}
import akka.util.Timeout
import akka.pattern._
import scala.language.postfixOps

class ActorLifeCycle extends Actor with ActorLogging {
  def receive = {
    case msg if msg == "hello" => log.info(s"Message sent=$msg")
      10/0

      val child=context.actorOf(Props[Child],name="Child")
           child !msg
  }

  override def preStart(){
    println("Prstart method is called")
  }

  override def postStop(){
    println("Poststop method is called")
  }

  override def preRestart(reason:Throwable,message:Option[Any]){
    println("preRestart method is called")
    println("reason="+reason)
  }
}

class Child extends Actor with ActorLogging{
  def receive = {
    case info => log.info(s"Message sent=$info")
  }
}

object ActorLifeCycle extends App {
  val system = ActorSystem("ActorLifeCycle")
  val actorLifeCycle = system.actorOf(Props[ActorLifeCycle], "LifeCycle")
actorLifeCycle ! "hello"

  system.stop(actorLifeCycle)
}