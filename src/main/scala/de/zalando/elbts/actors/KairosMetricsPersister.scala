package de.zalando.elbts.actors

import akka.actor.Actor
import org.kairosdb.client.HttpClient
import org.kairosdb.client.builder.MetricBuilder
import org.slf4j.LoggerFactory

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsPersister extends Actor with MetricsPersister with Logging{

  private val client = new HttpClient("http://localhost:8080")

  override def receive: Receive = {
    case metricBuilder: MetricBuilder => {
      client.pushMetrics(metricBuilder)
    }
    case msg => logger.info(s"Unknown msg: $msg")
  }
}

