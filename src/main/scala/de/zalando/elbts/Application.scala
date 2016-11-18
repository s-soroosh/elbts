package de.zalando.elbts

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import de.zalando.elbts.actors._
import de.zalando.elbts.messages.Run
import scaldi.Module
import scaldi.akka.AkkaInjectable

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
object Application extends App with RequestTimeout with AkkaInjectable {

  val config = ConfigFactory.load()

  implicit val module = new ActorModule :: new AkkaModule
  implicit val system = inject[ActorSystem]
  val queueActor = injectActorRef[QueueReader]


  queueActor ! Run()

}

trait RequestTimeout {

  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}

class AkkaModule extends Module {
  bind[ActorSystem] to ActorSystem("ELBTS") destroyWith (_.terminate())
}
