package de.zalando.elbts.messages

import org.joda.time.DateTime
import org.scalatest.WordSpec

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class LogItemSpec extends WordSpec {

  "fromELBString" should {
    "return defined object" in {
      val logItem = LogItem(new DateTime("2016-11-11T17:30:32.838+01:00"), "octopus-service-R1M75P3", "62.138.84.162:39399", "172.31.151.34:9000", "0.00003", "0.004707", "0.000017", 200, 200, 0, 111, HttpRequest("GET", "https", "service.octopus.zalan.do:443",
        "steering-points/975dffab-b2e0-47bc-9352-50d604b67b61/assignments", "context=CustomerHash:67a4f1affc337404c2765e73e717c655&context=AppDomain:1", "HTTP/1.1"), "Apache-HttpClient/4.5.1 (Java/1.7.0_80)", "ECDHE-RSA-AES128-SHA", "TLSv1")
      val sample = "2016-11-11T16:30:32.838457Z octopus-service-R1M75P3 62.138.84.162:39399 172.31.151.34:9000 0.00003 0.004707 0.000017 200 200 0 111 \"GET https://service.octopus.zalan.do:443/steering-points/975dffab-b2e0-47bc-9352-50d604b67b61/assignments?context=CustomerHash:67a4f1affc337404c2765e73e717c655&context=AppDomain:1 HTTP/1.1\" \"Apache-HttpClient/4.5.1 (Java/1.7.0_80)\" ECDHE-RSA-AES128-SHA TLSv1"
      val result = LogItem.fromELBString(sample)
      assert(logItem == result)
    }
  }


}
