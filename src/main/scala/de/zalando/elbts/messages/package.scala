package de.zalando.elbts

import awscala._

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
package object messages {

  case class LogItem(issueDate: DateTime, elb: String, clientHost: String, serverHost: String, requestProcessingTime: String, backendProcessingTime: String, responseProcessingTime: String, elbStatusCode: Int, backendStatusCode: Int, sentBytes: Int, request: HttpRequest, userAgent: String, sslCipher: String, sslProtocol: String)

  case class Run()

  case class LogFileDescriptor(bucketName: String, objectKey: String)

}
