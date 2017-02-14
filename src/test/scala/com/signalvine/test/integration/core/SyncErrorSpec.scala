package com.signalvine.test.integration.core

import com.signalvine.integration.core._
import play.api.libs.json.Json
import org.specs2.mutable.Specification
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}

class SyncErrorSpec extends Specification with JsonMatchers {
  "SyncError" should {
    val participantId = UUID.fromRaw[Participant](UUID.genRaw())
    val message = "This is an error message"
    val details = "Some details"

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    "serialize into JSON with all fields provided" >> {
      val o = new SyncError(participantId, message, details)
      val json = Json.toJson(o).toString

      json must anObjectWith(
        "participantId" -> participantId.toString,
        "message" -> message,
        "details" -> details
      )
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(s"""{"participantId": "${participantId.toString}", "message": "$message", "details": "$details"}""")
      val o = Json.fromJson[SyncError](json).get

      o.participantId mustEqual participantId
      o.message mustEqual message
      o.details mustEqual details
    }
  }
}
