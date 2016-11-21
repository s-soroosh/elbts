package de.zalando.elbts


/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
package object messages {

  case class Run()

  case class Flush()

  case class LogFileDescriptor(bucketName: String, objectKey: String)

}
