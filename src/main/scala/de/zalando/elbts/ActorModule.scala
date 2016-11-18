package de.zalando.elbts

import de.zalando.elbts.actors._
import org.kairosdb.client.builder.MetricBuilder
import scaldi.Module

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class ActorModule extends Module {
  bind [LoadBalancerLogParser] toProvider new ELBLogParser
  bind [MetricsBuilder] toProvider new KairosMetricsBuilder
  bind [MetricsPersister] toProvider new KairosMetricsPersister
  bind [QueueReader] toProvider new SQSReader
  bind [Tagger] toProvider new Tagger(TagConfiguration.fromConfig("tag-conf"))
  bind [SQSConfiguration] toProvider SQSConfiguration.fromConfig("sqs-conf")
  bind [S3Configuration] toProvider S3Configuration.fromConfig("s3-conf")
}
