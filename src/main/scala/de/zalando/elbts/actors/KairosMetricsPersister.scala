package de.zalando.elbts.actors

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import de.zalando.elbts.Logging
import org.kairosdb.client.HttpClient
import org.kairosdb.client.builder.MetricBuilder
import scaldi.akka.AkkaInjectable

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsPersister extends Actor with AkkaInjectable with MetricsPersister with Logging {

  private val kairosConfiguration: KairosConfiguration = inject[KairosConfiguration]
  private val client = new HttpClient(kairosConfiguration.url)

  override def receive: Receive = {
    case metricBuilder: MetricBuilder => {
      client.pushMetrics(metricBuilder)
    }
    case msg => logger.info(s"Unknown msg: $msg")
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

