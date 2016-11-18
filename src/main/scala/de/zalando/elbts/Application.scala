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

  //  private val sqsActor: ActorRef = system.actorOf(Props(new SQSReader(logParser)))
  //  private val kairosMetricsPersister: ActorRef = system.actorOf(Props(new KairosMetricsPersister))
  //  private val kairosMetricsBuilder: ActorRef = system.actorOf(Props(new KairosMetricsBuilder(kairosMetricsPersister, tagger)))
  //  private val logParser: ActorRef = system.actorOf(Props(new ELBLogParser(kairosMetricsBuilder)))

  queueActor ! Run()

  Thread.sleep(20000)
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
