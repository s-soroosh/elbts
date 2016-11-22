package de.zalando.elbts.actors

import java.io.{BufferedReader, InputStreamReader}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.BalancingPool
import awscala._
import awscala.s3.{S3, S3Object}
import awscala.sqs._
import com.typesafe.config.Config
import de.zalando.elbts.messages.{LogFileDescriptor, Run}
import play.api.libs.json.{JsArray, JsValue, Json}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class SQSReader(implicit injector: Injector) extends Actor with QueueReader with AkkaInjectable with ActorLogging {

  val target: ActorRef = context.actorOf(Props(new ELBLogParser).withRouter(BalancingPool(15)), "elb-log-parser")
  val sqsConfig: SQSConfiguration = inject[SQSConfiguration]
  val s3Config: S3Configuration = inject[S3Configuration]

  log.info("A new SQSReader has been created")

  implicit val sqs = SQS.at(sqsConfig.region)
  implicit val s3 = S3.at(s3Config.region)
  val queue = sqs.queue(sqsConfig.queueName).getOrElse(throw new Exception("elb-queue does not exist."))


  private def receiveMessage: Option[LogFileDescriptor] = {
    val messages: Seq[Message] = sqs.receiveMessage(queue, 1, 20)

    messages.headOption.map(m => {
      val body: JsValue = Json.parse(m.body)
      println("body: " + body)
      val s3Info = (body \ "Records").validate[JsArray].get.value.head \ "s3"
      val bucketName: String = (s3Info \ "bucket" \ "name").validate[String].getOrElse(throw new Exception("bucket name field does not exist"))
      val objectKey: String = (s3Info \ "object" \ "key").validate[String].getOrElse(throw new Exception("object key does not exist"))
      sqs.deleteMessage(m)
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
      message.flatMap(readS3File).map(s3obj => {
        val reader: BufferedReader = new BufferedReader(new InputStreamReader(s3obj.content))
        var line: String = reader.readLine()

        while (line != null) {
          target ! line
          line = reader.readLine()
        }
        reader.close()
        s3obj.content.close()
        s3obj.close()
        None
      })
      self ! Run()
    }
  }
}


case class SQSConfiguration(region: Region, queueName: String)

case class S3Configuration(region: Region)

object SQSConfiguration {

  def fromConfig(confgiName: String)(implicit config: Config): SQSConfiguration = {
    val sqsConfig = config.getConfig(confgiName)
    val regionName = sqsConfig.getString("region")
    val qName = sqsConfig.getString("queue-name")
    val region = Region(regionName)

    SQSConfiguration(region, qName)
  }
}

object S3Configuration {

  def fromConfig(configName: String)(implicit config: Config): S3Configuration = {
    val s3Config = config.getConfig(configName)
    val regionName = s3Config.getString("region")
    val region = Region(regionName)

    S3Configuration(region)
  }
}
