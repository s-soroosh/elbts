package de.zalando.elbts

import akka.actor.Actor
import de.zalando.elbts.messages.LogItem
import org.kairosdb.client.builder.{Metric, MetricBuilder}

import scala.collection.JavaConverters._

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class MetricsBuilder extends Actor {
  override def receive: Receive = {
    case logItem: LogItem => {

      val tags = Map("elb" -> logItem.elb, "endpoint" -> logItem.request.path).asJava
      val metric1 = new Metric("backend_response_time").addDataPoint(logItem.issueDate.getMillis).addTags(tags)
      val metric2 = new Metric("backend_status_code").addDataPoint(logItem.backendStatusCode).addTags(tags)
      val metric3 = new Metric("elb_status_code").addDataPoint(logItem.backendStatusCode).addTags(tags)
      val builder = new MetricBuilder()
      builder.getMetrics().add(metric1)
      builder.getMetrics().add(metric2)
      builder.getMetrics().add(metric3)

      //todo: builder.addMetric("overall_response_time",logItem.backendProcessingTime)

    }
  }
}
