package de.zalando.elbts

import org.scalatest.{FunSuite, WordSpec}

/**
  * @author ssarabadani <soroosh.sarabadani@zalando.de>
  */
class HttpRequestSpec extends WordSpec {

  "fromElbString" should {
    "return defined object" in {
      assert(HttpRequest.fromElbString("GET https://service.octopus.zalan.do:443/steering-points/0528b573-5fdb-4086-9ddd-fb2d9c9a4571/assignments?context=CustomerId%3A3027770189 HTTP/1.1") ==
        HttpRequest("GET", "https", "service.octopus.zalan.do:443", "steering-points/0528b573-5fdb-4086-9ddd-fb2d9c9a4571/assignments", "context=CustomerId%3A3027770189", "HTTP/1.1"))
    }
  }

}
