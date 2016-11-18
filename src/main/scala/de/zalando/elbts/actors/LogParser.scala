package de.zalando.elbts.actors

import akka.actor.{Actor, ActorRef}
import de.zalando.elbts.messages.LogItem

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */

class LogParser(target: ActorRef) extends Actor {
  override def receive: Receive = {
    case logString: String => {
      val item = LogItem.fromELBString(logString)
      target ! item
      println(item)
    }
    case msg => println("Unknown msg: " + msg)
  }
}



