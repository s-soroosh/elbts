package de.zalando.elbts

import akka.actor.Actor

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */

class LogParser extends Actor {
  override def receive: Receive = {
    case logString: String => {
      println("logging " + logString)
    }
    case msg => println("Unknown msg: " + msg)
  }
}



