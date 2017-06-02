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
    val colname = "test"
    val row = 1

    def anObjectWith(fields: (String, Matcher[JsonType])*): Matcher[String] =
      fields.map { case (name, matcher) => /(name).andHave(matcher): Matcher[String] }.reduceLeft(_ and _)

    "serialize into JSON with all fields provided" >> {
      val o = new SyncError(Some(participantId), Some(colname), Some(row), None, message, Some(details))
      val json = Json.toJson(o)
      val jsonString = json.toString

      jsonString must anObjectWith(
        "participantId" -> participantId.toString,
        "msg" -> message,
        "details" -> details,
        "row" -> 1,
        "colname" -> "test"
      )
      (json \ "col").asOpt[Int] mustEqual None
    }

    "deserialize into object with all fields provided" >> {
      val json = Json.parse(s"""{"participantId": "${participantId.toString}", "msg": "$message", "details": "$details", "colname":"$colname", "row":$row}""")
      val o = Json.fromJson[SyncError](json).get

      o.participantId.get mustEqual participantId
      o.colName.get mustEqual colname
      o.column mustEqual None
      o.row mustEqual Some(1)
      o.message mustEqual message
      o.details.get mustEqual details
    }
  }
}
