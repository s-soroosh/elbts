package de.zalando.elbts

import akka.actor.Actor
import de.zalando.elbts.messages.LogItem

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */

class LogParser extends Actor {
  override def receive: Receive = {
    case logString: String => {
      val item = LogItem.fromELBString(logString)
      println(item)
    }
    case msg => println("Unknown msg: " + msg)
  }
}



