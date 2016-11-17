package de.zalando.elbts

import akka.actor.Actor
import de.zalando.elbts.messages.LogItem
import org.kairosdb.client.builder.MetricBuilder

import scala.collection.JavaConverters._

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsBuilder extends Actor {
  override def receive: Receive = {
    case logItem: LogItem => {

      val tags = Map("elb" -> logItem.elb, "endpoint" -> logItem.request.path).asJava

      val builder = MetricBuilder.getInstance()
      builder.addMetric("backend_response_time").addDataPoint(logItem.issueDate.getMillis).addTags(tags)
      builder.addMetric("backend_status_code").addDataPoint(logItem.backendStatusCode).addTags(tags)
      builder.addMetric("elb_status_code").addDataPoint(logItem.backendStatusCode).addTags(tags)

    }
  }
}
