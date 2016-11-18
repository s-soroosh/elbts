package de.zalando.elbts.actors

import akka.actor.Actor
import de.zalando.elbts.messages.LogItem
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */

class ELBLogParser(implicit injector: Injector) extends Actor with LoadBalancerLogParser with AkkaInjectable {

  val target = injectActorRef[MetricsBuilder]

  override def receive: Receive = {
    case logString: String => {
      val item = LogItem.fromELBString(logString)
      target ! item
    }
    case msg => println("Unknown msg: " + msg)
  }
}



