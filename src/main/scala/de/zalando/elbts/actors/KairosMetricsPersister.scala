package de.zalando.elbts.actors

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import org.kairosdb.client.HttpClient
import org.kairosdb.client.builder.MetricBuilder
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsPersister(implicit injector: Injector) extends Actor with AkkaInjectable with MetricsPersister with ActorLogging {

  private val kairosConfiguration: KairosConfiguration = inject[KairosConfiguration]
  private val client = new HttpClient(kairosConfiguration.url)
  println("new KairosMetricsPersister")

  override def receive: Receive = {
    case metricBuilder: MetricBuilder => {
      Future {
        client.pushMetrics(metricBuilder)
      }
    }
    case msg => log.warning(s"Unknown msg: $msg")
  }
}


case class KairosConfiguration(url: String)

object KairosConfiguration {

  def fromConfig(configName: String)(implicit config: Config): KairosConfiguration = {
    val kairosConfig = config.getConfig(configName)
    val url = kairosConfig.getString("url")

    KairosConfiguration(url)
  }
}

