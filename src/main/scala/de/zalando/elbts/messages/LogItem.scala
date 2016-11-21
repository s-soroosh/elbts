package de.zalando.elbts.messages

import java.util.regex.{Matcher, Pattern}

import org.joda.time.DateTime

import scala.util.matching.Regex

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
case class LogItem(issueDate: DateTime, elb: String, clientHost: String, backendHost: String, requestProcessingTime: Double, backendProcessingTime: Double, responseProcessingTime: Double, elbStatusCode: Int, backendStatusCode: Int, receivedBytes: Int, sentBytes: Int, request: HttpRequest, userAgent: String, sslCipher: String, sslProtocol: String)

object LogItem {
  private val pattern = Pattern.compile("([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*) \"(.*)\" \"(.*)\" ([^\\p{javaSpaceChar}]*) ([^\\p{javaSpaceChar}]*)")

  def fromELBString(elbLog: String): LogItem = {
    val matcher: Matcher = pattern.matcher(elbLog)
    if (matcher.find()) {

      val issueDateStr = matcher.group(1)
      val elb = matcher.group(2)
      val clientHost = matcher.group(3)
      val backendHost = matcher.group(4)
      val requestProcessingTime = matcher.group(5)
      val backendProcessingTime = matcher.group(6)
      val responseProcessingTime = matcher.group(7)
      val elbCode = matcher.group(8)
      val backendCode = matcher.group(9)
      val receivedBytes = matcher.group(10)
      val sentBytes = matcher.group(11)
      val requestStr = matcher.group(12)
      val agent = matcher.group(13)
      val sslCipher = matcher.group(14)
      val sslProtocol = matcher.group(15)
      val matchEnd: Long = System.currentTimeMillis()

      //    val pattern(issueDateStr, elb, clientHost, backendHost, requestProcessingTime, backendProcessingTime, responseProcessingTime, elbCode, backendCode, receivedBytes, sentBytes, requestStr, agent, sslCipher, sslProtocol) = elbLog
      val issueDate = new DateTime(issueDateStr)
      val request = HttpRequest.fromElbString(requestStr)
      LogItem(issueDate, elb, clientHost, backendHost, requestProcessingTime.toDouble, backendProcessingTime.toDouble, responseProcessingTime.toDouble, elbCode.toInt, backendCode.toInt, receivedBytes.toInt, sentBytes.toInt, request, agent, sslCipher, sslProtocol)
    }
    else {
      throw new LogItemMatchException
    }
  }

  class LogItemMatchException extends Exception{
  }
}
