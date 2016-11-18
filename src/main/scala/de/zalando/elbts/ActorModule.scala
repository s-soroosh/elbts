package de.zalando.elbts

import com.typesafe.config.ConfigFactory
import de.zalando.elbts.actors._
import scaldi.Module

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class ActorModule extends Module {
  private implicit val config = ConfigFactory.load()
  bind [LoadBalancerLogParser] toProvider new ELBLogParser
  bind [MetricsBuilder] toProvider new KairosMetricsBuilder
  bind [MetricsPersister] toProvider new KairosMetricsPersister
  bind [QueueReader] toProvider new SQSReader
  bind [Tagger] toProvider new Tagger(TagConfiguration.fromConfig("tag-conf"))
  bind [SQSConfiguration] toProvider SQSConfiguration.fromConfig("sqs-conf")
  bind [S3Configuration] toProvider S3Configuration.fromConfig("s3-conf")
  bind [KairosConfiguration] toProvider KairosConfiguration.fromConfig("kairos-conf")
}
