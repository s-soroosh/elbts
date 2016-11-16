package de.zalando.elbts


import org.joda.time.DateTime

import scala.util.matching.Regex

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
package object messages {

  case class LogItem(issueDate: DateTime, elb: String, clientHost: String, backendHost: String, requestProcessingTime: String, backendProcessingTime: String, responseProcessingTime: String, elbStatusCode: Int, backendStatusCode: Int, receivedBytes: Int, sentBytes: Int, request: HttpRequest, userAgent: String, sslCipher: String, sslProtocol: String)

  object LogItem {
    private val pattern: Regex = "(.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) \"(.*)\" \"(.*)\" (.*) (.*)".r

    def fromELBString(elbLog: String): LogItem = {
      val pattern(issueDateStr, elb, clientHost, backendHost, requestProcessingTime, backendProcessingTime, responseProcessingTime, elbCode, backendCode, receivedBytes, sentBytes, requestStr, agent, sslCipher, sslProtocol) = elbLog
      val issueDate = new DateTime(issueDateStr)
      val request = HttpRequest.fromElbString(requestStr)
      LogItem(issueDate, elb, clientHost, backendHost, requestProcessingTime, backendProcessingTime, responseProcessingTime, elbCode.toInt, backendCode.toInt, receivedBytes.toInt, sentBytes.toInt, request, agent, sslCipher, sslProtocol)
    }
  }



  case class Run()

  case class LogFileDescriptor(bucketName: String, objectKey: String)

}
