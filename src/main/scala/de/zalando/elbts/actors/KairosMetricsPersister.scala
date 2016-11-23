package de.zalando.elbts.actors

import java.util

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import org.kairosdb.client.HttpClient
import org.kairosdb.client.builder.MetricBuilder
import org.kairosdb.client.response.Response
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class KairosMetricsPersister(implicit injector: Injector) extends Actor with AkkaInjectable with MetricsPersister with ActorLogging {

  private val kairosConfiguration: KairosConfiguration = inject[KairosConfiguration]
  private val client = new HttpClient(kairosConfiguration.url)
  println("new KairosMetricsPersister")

  override def receive: Receive = {
    case metricBuilder: MetricBuilder => {
      val pushFuture: Future[Response] = Future {
        client.pushMetrics(metricBuilder)
      }
      pushFuture.onFailure { case t: Throwable =>
        log.error(t, "Error in persisting metrics into kairosdb.")
      }
      pushFuture.onSuccess { case response: Response =>
        val errors: String = util.Arrays.deepToString(response.getErrors.toArray())
        log.info(s"kairosdb pushMetrics result is: ${response.getStatusCode} errors: $errors")
      }
    }
    case msg => log.warning(s"Unknown msg: $msg")
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

