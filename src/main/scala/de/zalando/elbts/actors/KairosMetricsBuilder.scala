package de.zalando.elbts.actors

import akka.actor.{Actor, ActorRef, Props}
import de.zalando.elbts.Tagger
import de.zalando.elbts.messages.LogItem
import org.kairosdb.client.builder.MetricBuilder
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.collection.JavaConverters._

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsBuilder(implicit injector: Injector) extends Actor with MetricsBuilder with AkkaInjectable {

  val target: ActorRef = injectActorRef[MetricsPersister]
  val tagger: Tagger = inject[Tagger]


  override def receive: Receive = {
    case logItem: LogItem => {
      val tags = tagger.tag(logItem).asJava

      val builder = MetricBuilder.getInstance()
      builder.addMetric("backend_response_time").addDataPoint(logItem.issueDate.getMillis, logItem.backendProcessingTime).addTags(tags)
      builder.addMetric("overall_response_time").addDataPoint(logItem.issueDate.getMillis, logItem.backendProcessingTime + logItem.responseProcessingTime + logItem.requestProcessingTime).addTags(tags)
      builder.addMetric("backend_status_code").addDataPoint(logItem.issueDate.getMillis, logItem.backendStatusCode).addTags(tags)
      builder.addMetric("elb_status_code").addDataPoint(logItem.issueDate.getMillis, logItem.backendStatusCode).addTags(tags)

      target ! builder
    }
  }
}
