package de.zalando.elbts

import java.util.regex.{Matcher, Pattern}

import com.typesafe.config.{Config, ConfigFactory}
import de.zalando.elbts.messages.LogItem
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */



class Tagger(tagConfiguration: TagConfiguration) {

  import Tagger._

  def tag(logItem: LogItem): Map[String, String] = {

    val result: Map[String, String] = defaultTags(logItem) ++ tagConfiguration.findTags(logItem).getOrElse(tagUnknown(logItem))
    result
  }

  private def defaultTags(logItem: LogItem): Map[String,String] = {
    Map("load-balancer" -> logItem.elb,
        "elb-status-code" -> logItem.elbStatusCode.toString,
        "backend-status-code" -> logItem.backendStatusCode.toString
    )
  }

  private def tagUnknown(logItem: LogItem): Map[String, String] = {
    logger.warn(s"LogItem $logItem  is unknown.")
    Map("known" -> "false")
  }
}

object Tagger {
  private val logger = LoggerFactory.getLogger(classOf[Tagger])
}


case class URLConfig(url: String, groupNames: Seq[String]) {
  private val regex = url.r(groupNames: _*)

  def tagIfMatch(url: String): Option[Map[String, String]] = {
    regex.findFirstMatchIn(url).map(m => {
      (("url" -> url) +: groupNames.map(gName => gName -> m.group(gName))).toMap
    })
  }
}

case class TagConfiguration(urlConfigs: URLConfig*) {
  def findTags(logItem: LogItem): Option[Map[String, String]] = {
    urlConfigs.toStream.map(_.tagIfMatch(logItem.request.path)).filter(_ != None).take(1).headOption.getOrElse(None)
  }

}

object TagConfiguration {
  val config: Config = ConfigFactory.load()

  def fromConfig(configName: String) = {
    val tagConfig: Config = config.getConfig(configName)
    val expresssionConfigList = tagConfig.getConfigList("expressions").asScala
    val urlConfigs = expresssionConfigList.map(config => {
      val urlPattern = config.getString("url-pattern")
      val groupNames = config.getStringList("group-names").asScala
      URLConfig(urlPattern, groupNames)
    })


    TagConfiguration(urlConfigs: _*)
  }
}


object A extends App {
  private val pattern: Pattern = Pattern.compile("(?<method>\\w+) (?<number>\\w+)")
  private val r: Regex = "(?<method>\\w+) (?<number>\\w+)".r("method", "number")

  private val value: Match = r.findFirstMatchIn("GET 101").get
  println(value.groupCount)
  println(value.group("method"))
  println(value.groupNames)
  private val matcher: Matcher = pattern.matcher("GET 101")
  val found = matcher.find()
  println(found)
  println(matcher.group("method"))
  println(matcher.group("number"))

  val urlConfig1 = URLConfig("""steering-points/(?<spid>.*)/assignments""", Seq("spid"))
//  val urlConfig2 = URLConfig("""/steerings/(?<sid>\.*/.*""", Seq("sid"))

//  val tagConfiguration = TagConfiguration(urlConfig1)


  private val ifMatch: Option[Map[String, String]] = urlConfig1.tagIfMatch("steering-points/21ef1e58-63e6-46a5-8ddb-ddca0036fcb3/assignments")
  println(ifMatch)


  //  val conf = TagConfiguration(r)

}