package de.zalando.elbts

import de.zalando.elbts.messages.{HttpRequest, LogItem}
import org.joda.time.DateTime
import org.scalatest.WordSpec

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class TaggerSpec extends WordSpec {

  val urlConfig1 = URLConfig("""steering-points/(?<spid>.*)/assignments""", Seq("spid"))
  //  val urlConfig2 = URLConfig("""/steerings/(?<sid>\.*/.*""", Seq("sid"))

  //  val tagConfiguration = TagConfiguration(urlConfig1)


  private val ifMatch: Option[Map[String, String]] = urlConfig1.tagIfMatch("steering-points/21ef1e58-63e6-46a5-8ddb-ddca0036fcb3/assignments")
  println(ifMatch)
  val tagConf = TagConfiguration(URLConfig("""steering-points/(?<spid>.*)/assignments""", Seq("spid")))
  val tagger = new Tagger(tagConf)


  "tagger" should {

    "return spid " in {
      val logItem = LogItem(new DateTime("2016-11-11T17:30:32.838+01:00"), "octopus-service-R1M75P3", "62.138.84.162:39399", "172.31.151.34:9000", 0.00003, 0.004707, 0.000017, 200, 200, 0, 111, HttpRequest("GET", "https", "service.octopus.zalan.do:443",
        "steering-points/975dffab-b2e0-47bc-9352-50d604b67b61/assignments", "context=CustomerHash:67a4f1affc337404c2765e73e717c655&context=AppDomain:1", "HTTP/1.1"), "Apache-HttpClient/4.5.1 (Java/1.7.0_80)", "ECDHE-RSA-AES128-SHA", "TLSv1")
      val tags: Map[String, String] = tagger.tag(logItem)

      assert(tags == Map("load-balancer" -> "octopus-service-R1M75P3", "url" -> "steering-points/(?<spid>.*)/assignments", "spid" -> "975dffab-b2e0-47bc-9352-50d604b67b61", "elb-status-code" -> "200", "backend-status-code" -> "200") )
    }

  }

}
