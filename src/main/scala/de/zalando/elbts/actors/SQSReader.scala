package de.zalando.elbts.actors

import java.io.{BufferedReader, InputStreamReader}

import akka.actor.{Actor, ActorRef}
import awscala._
import awscala.s3.{S3, S3Object}
import awscala.sqs._
import de.zalando.elbts.messages.{LogFileDescriptor, Run}
import play.api.libs.json.{JsArray, JsValue, Json}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */


class SQSReader(implicit injector: Injector) extends Actor with QueueReader with AkkaInjectable {

  val target: ActorRef = injectActorRef[LoadBalancerLogParser]

  implicit val sqs = SQS.at(Region.EU_CENTRAL_1)
  implicit val s3 = S3.at(Region.EU_CENTRAL_1)
  val queue = sqs.queue("elb-queue").getOrElse(throw new Exception("elb-queue does not exist."))


  private def receiveMessage: Option[LogFileDescriptor] = {

    val messages: Seq[Message] = sqs.receiveMessage(queue, 1, 20)

    messages.headOption.map(m => {
      val body: JsValue = Json.parse(m.body)
      println("body: " + body)
      val s3Info = (body \ "Records").validate[JsArray].get.value.head \ "s3"
      val bucketName: String = (s3Info \ "bucket" \ "name").validate[String].getOrElse(throw new Exception("bucker-name field does not exist"))
      val objectKey: String = (s3Info \ "object" \ "key").validate[String].getOrElse(throw new Exception("object key does not exist"))
      println(bucketName)
      println(objectKey)
      LogFileDescriptor(bucketName, objectKey)
    })

  }

  private def readS3File(logFileDescriptor: LogFileDescriptor): Option[S3Object] = {
    val s3Object: Option[S3Object] = s3.bucket(logFileDescriptor.bucketName).flatMap(_.get(logFileDescriptor.objectKey))

    s3Object
  }


  override def receive: Receive = {
    case Run() => {
      val message: Option[LogFileDescriptor] = receiveMessage

      message.flatMap(readS3File).flatMap(s3obj => {
        val reader: BufferedReader = new BufferedReader(new InputStreamReader(s3obj.content))
        Stream.continually(reader.readLine()).takeWhile(_ != null).foreach(line => target ! line)
        None
      })
      self ! Run()
    }
  }
}
