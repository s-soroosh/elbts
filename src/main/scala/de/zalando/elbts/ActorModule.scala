package de.zalando.elbts

import akka.actor.Props
import akka.routing.BalancingPool
import com.typesafe.config.ConfigFactory
import de.zalando.elbts.actors._
import scaldi.Module

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class ActorModule extends Module {

  private implicit val config = ConfigFactory.load()
  //todo: load balance support in di
//  binding toProvider   Props(new ELBLogParser).withRouter(BalancingPool(5))
  bind[MetricsBuilder] toProvider new KairosMetricsBuilder
  bind[MetricsPersister] toProvider new KairosMetricsPersister
  bind[QueueReader] toProvider new SQSReader
  bind[Tagger] toProvider new Tagger(TagConfiguration.fromConfig("tag-conf"))
  bind[SQSConfiguration] toProvider SQSConfiguration.fromConfig("sqs-conf")
  bind[S3Configuration] toProvider S3Configuration.fromConfig("s3-conf")
  bind[KairosConfiguration] toProvider KairosConfiguration.fromConfig("kairos-conf")
}
