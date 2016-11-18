package de.zalando.elbts

import org.slf4j.LoggerFactory

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
trait Logging {
  protected val logger = LoggerFactory.getLogger(this.getClass)
}
