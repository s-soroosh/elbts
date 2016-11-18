package de.zalando.elbts

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import de.zalando.elbts.actors.{KairosMetricsBuilder, KairosMetricsPersister, LogParser, SQSReader}
import de.zalando.elbts.messages.Run

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
object Application extends App with RequestTimeout {

  val config = ConfigFactory.load()

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  private val tagger = new Tagger(TagConfiguration.fromConfig("tag-conf"))

  private val sqsActor: ActorRef = system.actorOf(Props(new SQSReader(logParser)))
  private val kairosMetricsPersister: ActorRef = system.actorOf(Props(new KairosMetricsPersister))
  private val kairosMetricsBuilder: ActorRef = system.actorOf(Props(new KairosMetricsBuilder(kairosMetricsPersister, tagger)))
  private val logParser: ActorRef = system.actorOf(Props(new LogParser(kairosMetricsBuilder)))

  sqsActor ! Run()

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
