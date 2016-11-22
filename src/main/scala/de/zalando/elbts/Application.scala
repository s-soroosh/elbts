package de.zalando.elbts

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
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

  private val host: String = config.getString("http.host")
  private val port: Int = config.getInt("http.port")
  implicit val module = new ActorModule :: new AkkaModule
  implicit val system = inject[ActorSystem]
  implicit val ec = system.dispatcher
  val queueActor = injectActorRef[QueueReader]("queue-reader")
  val log = Logging(system, "elbts")


  private val route: Route = new RestRoutes().healthcheckRoute
  implicit val actorMaterializer = ActorMaterializer()

  val bindingFuture = Http().bindAndHandle(route, host, port)


  bindingFuture.map { binding =>
    log.info(s"API bound  to ${binding.localAddress}")
  }.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}", host, port)
  }
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
