package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import org.specs2.matcher.JsonMatchers

class DateTimeSpec extends Specification with JsonMatchers {
  "DateTime" should {
    val raw = "1991-02-13T10:10:43.771Z"

    "create a new instance with a string date" >> {
      val r = DateTime.parse(raw)

      r must beRight
      r.right.get.toString mustEqual raw
    }

    "return error message if date format is wrong" >> {
      val s = "foo-bar"
      val r = DateTime.parse(s)

      r must beLeft
      r.left.get mustEqual s"Invalid datetime; expected 'YYYY-MM-DDTHH:mm:ss.SSSZ', got: $s"
    }

    "serialize into JSON value" >> {
      val r = DateTime.parse(raw).right.get
      val json = Json.toJson(Json.obj("date" -> r)).toString

      json must /("date", raw)
    }
  }
}
