package de.zalando.elbts.messages

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
case class HttpRequest(method: String, scheme: String, domain: String, path: String, queryParameters: String, httpVersion: String)

object HttpRequest {
  private val pattern: Regex = "^([A-Z]*) (.*)://([^/]*)/(.*)\\?(.*) (.*)".r

  def fromElbString(elbHost: String): HttpRequest = {
    val pattern(method, scheme, domain, path, queryParams, httpVersion) = elbHost

    HttpRequest(method, scheme, domain, path, queryParams, httpVersion)
  }
}
