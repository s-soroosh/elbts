package de.zalando.elbts.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import de.zalando.elbts.Tagger
import de.zalando.elbts.messages.{Flush, LogItem}
import org.kairosdb.client.builder.MetricBuilder
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsBuilder(implicit injector: Injector) extends Actor with MetricsBuilder with AkkaInjectable with ActorLogging {

  val target: ActorRef = injectActorRef[MetricsPersister]("persister")
  val tagger: Tagger = inject[Tagger]
  println("new KairosMetricsBuilder")

  var builder = MetricBuilder.getInstance()

  context.system.scheduler.schedule(10 seconds, 15 seconds) {
    self ! Flush()
  }


  override def receive: Receive = {
    case logItem: LogItem => {
      val tags = tagger.tag(logItem).asJava

      builder.addMetric("backend_response_time").addDataPoint(logItem.issueDate.getMillis, logItem.backendProcessingTime).addTags(tags)
      builder.addMetric("overall_response_time").addDataPoint(logItem.issueDate.getMillis, logItem.backendProcessingTime + logItem.responseProcessingTime + logItem.requestProcessingTime).addTags(tags)
      builder.addMetric("backend_status_code").addDataPoint(logItem.issueDate.getMillis, logItem.backendStatusCode).addTags(tags)
      builder.addMetric("elb_status_code").addDataPoint(logItem.issueDate.getMillis, logItem.backendStatusCode).addTags(tags)

      if (builder.getMetrics.size() > 800) {
        sendMetricsToPersister

      }
    }

    case Flush() => {
      val metricsSize: Int = builder.getMetrics.size()
      if (metricsSize > 0) {
        log.debug(s" $metricsSize metrics have been sent to persister in flushing step.")
        sendMetricsToPersister
      }
    }
  }

  def sendMetricsToPersister: Unit = {
    target ! builder
    builder = MetricBuilder.getInstance()
  }
}
