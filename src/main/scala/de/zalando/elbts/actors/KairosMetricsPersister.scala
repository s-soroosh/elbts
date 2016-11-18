package de.zalando.elbts.actors

import akka.actor.Actor
import org.kairosdb.client.builder.MetricBuilder

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsPersister extends Actor with MetricsPersister {
  override def receive: Receive = {
    case metricBuilder: MetricBuilder => {
      println("persisting " + metricBuilder)
    }
    case msg => println("unknown msg " + msg)
  }
}
